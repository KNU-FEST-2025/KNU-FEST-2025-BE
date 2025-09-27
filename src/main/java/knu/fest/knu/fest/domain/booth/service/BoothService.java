package knu.fest.knu.fest.domain.booth.service;

import knu.fest.knu.fest.domain.booth.controller.dto.BoothCreateRequest;
import knu.fest.knu.fest.domain.booth.controller.dto.BoothDetailResponse;
import knu.fest.knu.fest.domain.booth.controller.dto.BoothUpdateRequest;
import knu.fest.knu.fest.domain.booth.entity.Booth;
import knu.fest.knu.fest.domain.booth.repository.BoothRepository;
import knu.fest.knu.fest.global.exception.CommonException;
import knu.fest.knu.fest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoothService {

    private final BoothRepository boothRepository;

    @Transactional
    public BoothDetailResponse create(BoothCreateRequest request) {
        if (boothRepository.existsByBoothNumber(request.boothNumber())) {
            throw new CommonException(ErrorCode.ALREADY_EXIST_BOOTH_NUMBER);
        }
        Booth booth = request.toEntity();

        return BoothDetailResponse.of(boothRepository.save(booth));
    }

    @Transactional
    public BoothDetailResponse update(Long id, BoothUpdateRequest request) {
        Booth booth = boothRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_BOOTH));

        booth.update(request.name(), request.description());

        return BoothDetailResponse.of(booth);
    }

    @Transactional(readOnly = true)
    public BoothDetailResponse get(Long id) {
        Booth booth = boothRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_BOOTH));

        return BoothDetailResponse.of(booth);
    }

/**
 * 목록 조회 API는 웨이팅 기능 개발 이후에 구현 예정
 *
    @Transactional(readOnly = true)
    public List<BoothDetailResponse> list() {
        List<Booth> booths = boothRepository.findAll();
        return booths.stream().map(BoothDetailResponse::of).toList();
    }
    */
}
