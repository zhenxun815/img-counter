package cn.yiheng;

import cn.yiheng.unique.AlreadyLockedException;
import cn.yiheng.unique.JUnique;
import cn.yiheng.utils.FXMLUtils;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Yiheng
 * @create 4/15/2019
 * @since 1.0.0
 */
@SpringBootApplication
public class Main extends Application {

    static Logger logger = LoggerFactory.getLogger(Main.class);
    public static ConfigurableApplicationContext springContext;
    public static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        initPrimaryStageSize();
        stage.setOnCloseRequest(event -> System.exit(0));

        FXMLUtils.loadWindow(stage, "/static/fxml/main.fxml");
    }

    /**
     * 初始最大化窗口,固定窗体大小
     */
    private void initPrimaryStageSize() {

        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double minX = visualBounds.getMinX();
        double minY = visualBounds.getMinY();
        //double maxX = visualBounds.getMaxX();
        //double maxY = visualBounds.getMaxY();
        double width = visualBounds.getWidth();
        double height = visualBounds.getHeight();

        //logger.info("minX: " + minX + ", minY: " + minY);
        //logger.info("maxX: " + maxX + ", maxY: " + maxY);
        //logger.info("width: " + width + ", height: " + height);
        stage.setX(minX + width / 2 - 310);
        stage.setY(minY + height / 2 - 134);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
    }

    @Override
    public void init() throws Exception {
        super.init();
        Platform.setImplicitExit(false);
        springContext = SpringApplication.run(Main.class);
    }


    @Override
    public void stop() {
        springContext.stop();
        try {
            super.stop();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String appId = "YIHENG-IMG-COUNTER";
        boolean alreadyRunning;
        try {
            JUnique.acquireLock(appId, message -> {
                System.out.println("get message: " + message);
                return null;
            });
            alreadyRunning = false;
        } catch (AlreadyLockedException e) {
            alreadyRunning = true;
        }

        if (alreadyRunning) {
            for (int i = 0; i < args.length; i++) {
                JUnique.sendMessage(appId, "call_window");
            }
        } else {
            launch(args);
        }
    }

}
