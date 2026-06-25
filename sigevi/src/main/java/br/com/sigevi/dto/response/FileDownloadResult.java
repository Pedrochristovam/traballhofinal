package br.com.sigevi.dto.response;

import org.springframework.core.io.Resource;

public record FileDownloadResult(Resource resource, String contentType, String filename) {
}
