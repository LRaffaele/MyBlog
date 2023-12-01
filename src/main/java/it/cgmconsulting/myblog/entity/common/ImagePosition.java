package it.cgmconsulting.myblog.entity.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum ImagePosition {


    // PRE = Preview - immagine di anteprima
    // HDR = Header - immagine principale del post
    // CON = Content - immagine secondaria nel contenuto del post

    PRE("Preview",200, 200, 10240L, 1),
    HDR("Header",600, 300, 102400L, 1),
    CON("Content",400, 200, 20400L, 5);

    private String description;
    private int width;
    private int height;
    private long size;
    private int maxImages;
}
