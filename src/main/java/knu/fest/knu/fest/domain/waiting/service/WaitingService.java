package knu.fest.knu.fest.domain.waiting.service;

import io.micrometer.common.lang.Nullable;
import knu.fest.knu.fest.domain.booth.entity.Booth;
import knu.fest.knu.fest.domain.booth.repository.BoothRepository;
import knu.fest.knu.fest.domain.booth.service.BoothSseService;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final BoothRepository boothRepository;
    private final WaitingCacheService waitingCacheService;
    private final WaitingSseNotifier waitingSseNotifier;
    private final BoothSseService boothSseService;

    @Transactional
    public WaitingRegisterResponse registerWaiting(WaitingRegisterRequest request) {
        Booth booth = boothRepository.findById(request.boothId()).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_BOOTH));

        Waiting w = request.toEntity(booth);
        waitingRepository.save(w);

        // WaitingCount + 1
        booth.addWaiting();
        boothRepository.save(booth);
        Long waitingId = w.getId();

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                waitingCacheService.enqueue(request.boothId(), waitingId);
                waitingSseNotifier.notifyCount(request.boothId(), waitingCacheService.teamCount(request.boothId()));
            }
        });

        boothSseService.sendAllBoothUpdate(booth.getId(), booth.getLikeCount(), booth.getWaitingCount());

        return WaitingRegisterResponse.of(waitingId, request.nickName(), request.waitingPeopleNum());
    }


    @Transactional
    public WaitingStatusResponse waitingCancel(Long boothId, Long waitingId) {
        Waiting waiting = waitingRepository.findByIdAndBoothId(waitingId, boothId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_WAITING));

        Booth booth = boothRepository.findById(boothId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_BOOTH));

        waiting.cancel();

        // WaitingCount -1
        booth.removeWaiting();
        boothRepository.save(booth);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                waitingCacheService.remove(boothId, waitingId, "CANCELLED");
                waitingSseNotifier.notifyCount(boothId, waitingCacheService.teamCount(boothId));
            }
        });

        boothSseService.sendAllBoothUpdate(booth.getId(), booth.getLikeCount(), booth.getWaitingCount());

        return WaitingStatusResponse.of(waiting);
    }

    @Transactional
    public WaitingStatusResponse waitingComplete(Long boothId, Long waitingId) {
        Waiting waiting = waitingRepository.findByIdAndBoothId(waitingId, boothId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_WAITING));

        Booth booth = boothRepository.findById(boothId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_BOOTH));

        waiting.complete();

        // WaitingCount -1
        booth.removeWaiting();
        boothRepository.save(booth);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                waitingCacheService.remove(boothId, waitingId, "DONE");
                waitingSseNotifier.notifyCount(boothId, waitingCacheService.teamCount(boothId));
            }
        });

        boothSseService.sendAllBoothUpdate(booth.getId(), booth.getLikeCount(), booth.getWaitingCount());

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

        waiting.update(request.nickName(), request.phone(), request.waitingPeopleNum());
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
        // ① WAITING: 기존처럼 Redis 순서 기반
        if (statusFilter == null || statusFilter == WaitingStatus.WAITING) {
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
                // WAITING만 화면에 노출 (statusFilter==null도 WAITING 탭이라고 가정)
                if (w.getStatus() != WaitingStatus.WAITING) continue;

                result.add(PrivateWaitingListItemResponse.of(
                        w.getId(),
                        w.getNickName(),
                        w.getPhone(),         // 필요시 마스킹 로직 적용
                        w.getCreatedAt(),
                        w.getStatus(),
                        w.getWaitingPeopleNum(),
                        order++               // Redis 순서 = 대기 순번
                ));
            }
            return result;
        }

        // ② DONE / CANCELLED: DB 기준 조회 (createdAt 오름차순)
        List<Waiting> rows = waitingRepository
                .findAllByBoothIdAndStatusOrderByCreatedAtAsc(boothId, statusFilter);

        AtomicInteger order = new AtomicInteger(1); // '순번' 개념이 없으므로, 시간순으로 가상의 순번 부여
        return rows.stream()
                .map(w -> PrivateWaitingListItemResponse.of(
                        w.getId(),
                        w.getNickName(),
                        w.getPhone(),
                        w.getCreatedAt(),
                        w.getStatus(),
                        w.getWaitingPeopleNum(),
                        order.getAndIncrement()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WaitingMyPhoneResponse> waitingPhoneNum(String phone) {
        List<Waiting> waitings = waitingRepository.findAllByPhoneAndStatus(phone, WaitingStatus.WAITING);

        List<WaitingMyPhoneResponse> response = new ArrayList<>();
        for (Waiting w: waitings){
            long boothId = w.getBooth().getId();
            Booth booth = boothRepository.findById(boothId).orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_BOOTH));
            String boothName = booth.getName();
            String imagePath = null;
            if (booth.getImagePath() != null){
                imagePath = booth.getImagePath().get(0);
            }

            int cnt = 1;

            List<Waiting> ws = waitingRepository
                    .findAllByBoothIdAndStatusOrderByCreatedAtAsc(boothId, WaitingStatus.WAITING);

            for (Waiting ww: ws) {
                if (ww.getPhone().equals(phone)){
                    break;
                }
                cnt += 1;
            }

            WaitingMyPhoneResponse re = WaitingMyPhoneResponse.builder()
                    .boothId(boothId)
                    .boothName(boothName)
                    .imagePath(imagePath)
                    .waitingNum(cnt)
                    .build();
            response.add(re);
        }

        return response;

    }
}
