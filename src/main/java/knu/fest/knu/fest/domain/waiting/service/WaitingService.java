package knu.fest.knu.fest.domain.waiting.service;

import io.micrometer.common.lang.Nullable;
import knu.fest.knu.fest.domain.booth.entity.Booth;
import knu.fest.knu.fest.domain.booth.repository.BoothRepository;
import knu.fest.knu.fest.domain.waiting.controller.dto.*;
import knu.fest.knu.fest.domain.waiting.entity.Waiting;
import knu.fest.knu.fest.domain.waiting.entity.WaitingStatus;
import knu.fest.knu.fest.domain.waiting.repository.WaitingRepository;
import knu.fest.knu.fest.domain.waiting.sse.WaitingSseNotifier;
import knu.fest.knu.fest.global.exception.CommonException;
import knu.fest.knu.fest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final BoothRepository boothRepository;
    private final WaitingCacheService waitingCacheService;
    private final WaitingSseNotifier waitingSseNotifier;

    @Transactional
    public WaitingRegisterResponse registerWaiting(WaitingRegisterRequest request) {
        Booth booth = boothRepository.findById(request.boothId()).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_BOOTH));

        Waiting w = request.toEntity(booth);
        waitingRepository.save(w);
        Long waitingId = w.getId();

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                waitingCacheService.enqueue(request.boothId(), waitingId);
                waitingSseNotifier.notifyCount(request.boothId(), waitingCacheService.teamCount(request.boothId()));
            }
        });
        return WaitingRegisterResponse.of(waitingId, request.nickName());
    }


    @Transactional
    public WaitingStatusResponse waitingCancel(Long boothId, Long waitingId) {
        Waiting waiting = waitingRepository.findByIdAndBoothId(waitingId, boothId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_WAITING));

        waiting.cancel();

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                waitingCacheService.remove(boothId, waitingId, "CANCELLED");
                waitingSseNotifier.notifyCount(boothId, waitingCacheService.teamCount(boothId));
            }
        });

        return WaitingStatusResponse.of(waiting);
    }

    @Transactional
    public WaitingStatusResponse waitingComplete(Long boothId, Long waitingId) {
        Waiting waiting = waitingRepository.findByIdAndBoothId(waitingId, boothId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_WAITING));

        waiting.complete();

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                waitingCacheService.remove(boothId, waitingId, "DONE");
                waitingSseNotifier.notifyCount(boothId, waitingCacheService.teamCount(boothId));
            }
        });

        return WaitingStatusResponse.of(waiting);
    }

    @Transactional
    public WaitingStatusResponse waitingUpdate(Long boothId, Long waitingId, WaitingUpdateRequest request) {
        Waiting waiting = waitingRepository.findByIdAndBoothId(waitingId, boothId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_WAITING));

        // WAITING 상태에서만 수정 허용
        if (waiting.getStatus() != WaitingStatus.WAITING) {
            throw new CommonException(ErrorCode.INVALID_ARGUMENT);
        }

        waiting.update(request.nickName(), request.phone());
        return WaitingStatusResponse.of(waiting);
    }

    /**
     * 부스별 대기 명단 조회 (닉네임, 상태)
     */
    @Transactional(readOnly = true)
    public List<PublicWaitingListItemResponse> waitingListPublicQueue(Long boothId) {
        List<Long> ids = waitingCacheService.rangeAll(boothId);
        if (ids.isEmpty()) return List.of();

        List<Waiting> rows = waitingRepository.findAllByIdIn(ids);
        Map<Long, Waiting> map = rows.stream()
                .collect(Collectors.toMap(Waiting::getId, w -> w));

        List<PublicWaitingListItemResponse> result = new ArrayList<>(ids.size());
        for (int order = 0; order < ids.size(); order++) {
            Long id = ids.get(order);
            Waiting waiting = map.get(id);
            if (waiting == null) {
                log.warn("Redis queue mismatch: waitingId={} not found in DB (boothId={})", id, boothId);
                continue;
            }
            result.add(PublicWaitingListItemResponse.of(
                    waiting.getNickName(),
                    waiting.getStatus(),
                    order + 1 // 순번
            ));
        }
        return result;
    }

    /**
     *  매니저 전용: 전화번호/생성시각 포함, 상태 필터 옵션
     */
    @Transactional(readOnly = true)
    public List<PrivateWaitingListItemResponse> waitingListManagerQueue(
            Long boothId,
            @Nullable WaitingStatus statusFilter
    ) {
        List<Long> ids = waitingCacheService.rangeAll(boothId);
        if (ids.isEmpty()) return List.of();

        List<Waiting> rows = waitingRepository.findAllByIdIn(ids);
        Map<Long, Waiting> map = rows.stream()
                .collect(Collectors.toMap(Waiting::getId, w -> w));

        List<PrivateWaitingListItemResponse> result = new ArrayList<>(ids.size());
        int order = 1;
        for (Long id : ids) {
            Waiting w = map.get(id);
            if (w == null) {
                log.warn("Redis queue mismatch: waitingId={} not found in DB (boothId={})", id, boothId);
                continue;
            }
            if (statusFilter != null && w.getStatus() != statusFilter) continue;

            result.add(PrivateWaitingListItemResponse.of(
                    w.getId(),          // waitingId
                    w.getNickName(),
                    w.getPhone(),       // 매니저는 전체 번호 조회 가능 (필요 시 마스킹)
                    w.getCreatedAt(),
                    w.getStatus(),
                    order++             // 대기 순번 (1부터)
            ));
        }
        return result;
    }

}
