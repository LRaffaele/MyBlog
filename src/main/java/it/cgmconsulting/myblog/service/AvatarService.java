package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Avatar;
import it.cgmconsulting.myblog.entity.AvatarId;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.repository.AvatarRepository;
import it.cgmconsulting.myblog.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AvatarService {
    @Value("${app.avatar.size}")
    long avatarMaxSize;
    @Value("${app.avatar.width}")
    int avatarMaxWidth;
    @Value("${app.avatar.height}")
    int avatarMaxHeight;
    @Value("${app.avatar.extensions}")
    String[] avatarExtensions;

    private final AvatarRepository avatarRepository;


    private boolean checkSize(MultipartFile file){
        if(file.getSize() > avatarMaxSize || file.isEmpty())
            return false;
        return true;
    }

    protected BufferedImage fromMultiPartFiletoBufferedImage(MultipartFile file){
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file.getInputStream());
            return bufferedImage;
        } catch (IOException e) {
            return null;
        }
    }

    private boolean checkDimension(MultipartFile file){
        BufferedImage image = fromMultiPartFiletoBufferedImage(file);
        if (image != null) {
            if (image.getHeight() > avatarMaxHeight || image.getWidth() > avatarMaxWidth)
                return false;
            return true;
        } else
            return false;
    }

    private boolean  checkFileExtension(MultipartFile file){

        String filename = file.getOriginalFilename(); //pippo.gif
        String ext = null;
        try{
            ext = filename.substring(filename.lastIndexOf(".") + 1);
            if(Arrays.stream(avatarExtensions).anyMatch(ext::equalsIgnoreCase))
                return true;
        } catch (NullPointerException e){
            return false;
        }
        return false;
    }

    public ResponseEntity<?> avatar (MultipartFile file, UserPrincipal principal) throws IOException{
        if(!checkSize(file))
            return new ResponseEntity("File too big", HttpStatus.BAD_REQUEST);
        if(!checkDimension(file))
            return new ResponseEntity("Image too large. Images must be 100x100", HttpStatus.BAD_REQUEST);
        if(!checkFileExtension(file))
            return new ResponseEntity("File type not allowed", HttpStatus.BAD_REQUEST);

        Avatar avatar = new Avatar(new AvatarId(new User(principal.getId())), file.getOriginalFilename(), file.getContentType(), file.getBytes());
        avatarRepository.save(avatar);
        return new ResponseEntity<>("Avatar successfully uploaded", HttpStatus.OK);
    }
}
