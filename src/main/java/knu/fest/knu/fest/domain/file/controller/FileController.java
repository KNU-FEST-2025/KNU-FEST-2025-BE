package knu.fest.knu.fest.domain.file.controller;


import knu.fest.knu.fest.domain.file.dtos.UploadImageDto;
import knu.fest.knu.fest.domain.file.enums.ImageType;
import knu.fest.knu.fest.domain.file.service.FileService;
import knu.fest.knu.fest.global.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/image", consumes = "multipart/form-data")
    public ResponseDto<UploadImageDto> uploadImage(@RequestPart("image") MultipartFile image,
                                                   @RequestParam("type") ImageType type) {
        String url = fileService.store(image, type);
        return ResponseDto.ok(new UploadImageDto(url));
    }
}
