package cn.yiheng.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

import static cn.yiheng.Main.springContext;

/**
 * @author Yiheng
 * @create 3/22/2019
 * @since 1.0.0
 */
public class FXMLUtils {


    /**
     * 打开新窗口
     *
     * @param stage
     * @param url
     * @return
     */
    public static Stage loadWindow(Stage stage, String url) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FXMLUtils.class.getResource(url));
            fxmlLoader.setControllerFactory(springContext::getBean);
            Parent parentNode = fxmlLoader.load();
            loadScene(stage, parentNode);
            stage.show();
            return stage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 向{@link Stage Stage}加载{@link Scene Scene}
     *
     * @param stage
     * @param parentNode
     */
    private static void loadScene(Stage stage, Parent parentNode) {
        Scene scene = new Scene(parentNode, Color.TRANSPARENT);
        scene.getStylesheets().add(PathUtils.toExternalForm("/static/css/fx_root.css"));
        stage.setScene(scene);
        stage.getIcons().add(new Image(PathUtils.toExternalForm("/static/img/logo.png")));
    }
}
