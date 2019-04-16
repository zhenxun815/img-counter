package cn.yiheng.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.awt.*;

/**
 * @author Yiheng
 * @create 4/15/2019
 * @since 1.0.0
 */
@Getter
@Setter
@RequiredArgsConstructor(staticName = "of")
public class ImgInfo {

    @NonNull
    private long size;

    @NonNull
    private String fileName;

    @NonNull
    private Dimension resolution;
}
