package knu.fest.knu.fest.domain.notice.controller;

import knu.fest.knu.fest.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
}
