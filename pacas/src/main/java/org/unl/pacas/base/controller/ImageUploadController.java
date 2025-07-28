package org.unl.pacas.base.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;

import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ImageUploadController {
    
    @PostMapping(value = "/upload", 
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE, 
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
    
        Map<String, String> response = new HashMap<>();
        
        try {
            if (file == null || file.isEmpty()) {
                response.put("error", "No se recibió ningún archivo");
                return ResponseEntity.badRequest().body(response);
            }

            String contentType = file.getContentType();
          
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("error", "Solo se permiten imágenes");
                return ResponseEntity.badRequest().body(response);
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                response.put("error", "El archivo es demasiado grande (máximo 5MB)");
                return ResponseEntity.badRequest().body(response);
            }

            //RUTA themes/imagenes
            String folder = "src/main/frontend/themes/imagenes/";
            Path folderPath = Paths.get(folder);
            
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
                //System.out.println("Directorio creado: " + folderPath.toAbsolutePath());
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.trim().isEmpty()) {
                originalFilename = "imagen_" + System.currentTimeMillis();
            }
            
            String sanitizedName = originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
            String filename = System.currentTimeMillis() + "_" + sanitizedName;

            Path filePath = folderPath.resolve(filename);
            Files.write(filePath, file.getBytes());

            if (Files.exists(filePath)) {
                response.put("filename", filename);
                response.put("status", "success");
                response.put("path", "/public/img/" + filename);

                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Error al guardar el archivo");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (Exception e) {
            System.err.println("❌ Error en upload: " + e.getMessage());
            e.printStackTrace();
            
            response.put("error", "Error del servidor: " + e.getMessage());
            response.put("details", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}