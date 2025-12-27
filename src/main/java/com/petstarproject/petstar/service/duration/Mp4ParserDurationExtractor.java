package com.petstarproject.petstar.service.duration;

import com.petstarproject.petstar.exception.InvalidVideoFormatException;
import com.petstarproject.petstar.exception.VideoDurationExtractFailedException;
import com.petstarproject.petstar.exception.VideoSourceRequiredException;
import org.mp4parser.IsoFile;
import org.mp4parser.boxes.iso14496.part12.MovieBox;
import org.mp4parser.boxes.iso14496.part12.MovieHeaderBox;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

@Component
public class Mp4ParserDurationExtractor implements VideoDurationExtractor{
    @Override
    public int extractDurationSec(MultipartFile videoSource) {
        if (videoSource == null || videoSource.isEmpty()) {
            throw new VideoSourceRequiredException("Video source file is required");
        }

        if (!isMp4(videoSource)) {
            throw new InvalidVideoFormatException("Only MP4 is supported");
        }

        Path temp = null;
        try {
            temp = saveToTempFile(videoSource, ".mp4");
            return extractDurationFromMp4(temp);
        } finally {
            deleteQuietly(temp);
        }
    }

    private boolean isMp4(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && contentType.equalsIgnoreCase("video/mp4")) return true;

        String name = file.getOriginalFilename();
        return name != null && name.toLowerCase().endsWith(".mp4");
    }

    private Path saveToTempFile(MultipartFile file, String suffix) {
        try {
            Path temp = Files.createTempFile("upload-", suffix);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
            }
            return temp;
        } catch (IOException e) {
            throw new VideoDurationExtractFailedException("Failed to save temp file", e);
        }
    }

    private int extractDurationFromMp4(Path mp4Path) {
        try (FileChannel fc = FileChannel.open(mp4Path, StandardOpenOption.READ);
             IsoFile isoFile = new IsoFile(fc)) {

            MovieBox moov = isoFile.getBoxes(MovieBox.class).stream()
                    .findFirst()
                    .orElseThrow(() -> new VideoDurationExtractFailedException("Invalid MP4: moov box not found"));

            MovieHeaderBox mvhd = moov.getMovieHeaderBox();
            long duration = mvhd.getDuration();
            long timescale = mvhd.getTimescale();

            if (timescale <= 0) {
                throw new VideoDurationExtractFailedException("Invalid MP4: timescale is invalid");
            }

            double seconds = (double) duration / (double) timescale;
            return (int) Math.round(seconds);

        } catch (IOException e) {
            throw new VideoDurationExtractFailedException("Failed to parse MP4 duration", e);
        }
    }

    private void deleteQuietly(Path path) {
        if (path == null) return;
        try { Files.deleteIfExists(path); } catch (IOException ignored) {}
    }
}
