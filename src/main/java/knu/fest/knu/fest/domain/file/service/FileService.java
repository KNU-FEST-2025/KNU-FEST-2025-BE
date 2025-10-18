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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final LostItemRepository lostItemRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final Set<String> ALLOWED = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    @Transactional
    public String store(MultipartFile file, ImageType type) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 존재하지 않습니다.");
        }

        String contentType = file.getContentType();

        try {
            LocalDate today = LocalDate.now();
            Path dataDir = Paths.get(uploadDir,
                    String.valueOf(type))
                    .toAbsolutePath().normalize();

            Files.createDirectories(dataDir);

            // 확장자 추출
            String ext = switch(contentType.toLowerCase()) {
                case "image/jpeg" -> ".jpg";
                case "image/png"  -> ".png";
                case "image/gif"  -> ".gif";
                case "image/webp" -> ".webp";
                default -> "";
            };

            String filename = UUID.randomUUID().toString().replace("-", "") + ext;
            Path target = dataDir.resolve(filename).normalize();

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return target.toString();
        } catch (IOException e) {
            throw new RuntimeException("파일 저장중 에러 발생",e);
        }
    }

    @Transactional
    public ResponseEntity<Resource> getImage(Long id) throws IOException {
        LostItem item = lostItemRepository.findById(id).orElseThrow();
        String p = item.getImagePath();

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
