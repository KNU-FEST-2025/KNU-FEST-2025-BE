package knu.fest.knu.fest.domain.file.service;


import jakarta.transaction.Transactional;
import knu.fest.knu.fest.domain.file.enums.ImageType;
import knu.fest.knu.fest.domain.lostItem.entity.LostItem;
import knu.fest.knu.fest.domain.lostItem.repository.LostItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FileService {

    private final LostItemRepository lostItemRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.schema}")
    private String schema;

    private static final Set<String> ALLOWED = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    @Transactional
    public List<String> store(List<MultipartFile> files, ImageType type) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 존재하지 않습니다.");
        }

        try {
            // 저장 디렉터리 만들기: <uploadDir>/<type>
            Path dataDir = Paths.get(uploadDir, String.valueOf(type))
                    .toAbsolutePath()
                    .normalize();
            Files.createDirectories(dataDir);

            List<String> savedPaths = new ArrayList<>();

            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) {
                    continue; // 비어있는 항목 무시 (필요시 예외로 바꿔도 됨)
                }

                // 1) 확장자 결정
                String contentType = Optional.ofNullable(file.getContentType()).orElse("").toLowerCase();
                String ext = switch (contentType) {
                    case "image/jpeg" -> ".jpg";
                    case "image/png"  -> ".png";
                    case "image/gif"  -> ".gif";
                    case "image/webp" -> ".webp";
                    default -> "";
                };

                if (ext.isEmpty()) {
                    // contentType으로 못 찾으면 원본 파일명에서 확장자 추출
                    String original = Optional.ofNullable(file.getOriginalFilename()).orElse("");
                    int dot = original.lastIndexOf('.');
                    if (dot > -1 && dot < original.length() - 1) {
                        ext = "." + original.substring(dot + 1).toLowerCase();
                    }
                }

                if (ext.isEmpty()) {
                    throw new IllegalArgumentException("지원하지 않는 이미지 형식이거나 확장자를 확인할 수 없습니다: "
                            + file.getOriginalFilename());
                }

                // 2) 고유 파일명 생성
                String filename = UUID.randomUUID().toString().replace("-", "") + ext;
                Path target = dataDir.resolve(filename).normalize();

                // 3) 저장
                try (InputStream in = file.getInputStream()) {
                    Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                }

                savedPaths.add(schema + target);
            }

            if (savedPaths.isEmpty()) {
                throw new IllegalArgumentException("유효한 업로드 파일이 없습니다.");
            }

            return savedPaths;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional
    public ResponseEntity<Resource> getImage(String p) throws IOException {
        Path path = Paths.get(p).toAbsolutePath().normalize();
        System.out.println(path);

        String ct = Files.probeContentType(path);
        if (ct == null) ct = MediaType.APPLICATION_OCTET_STREAM_VALUE;

        Resource res = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(ct))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(path.getFileName().toString(), StandardCharsets.UTF_8).build().toString())
                .body(res);
    }
}
