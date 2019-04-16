package cn.yiheng.controller;

import cn.yiheng.Main;
import cn.yiheng.model.ImgInfo;
import cn.yiheng.task.CollectWorkerTask;
import cn.yiheng.utils.FileUtils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Yiheng
 * @create 3/22/2019
 * @since 1.0.0
 */
@RestController
public class CounterController {

    Logger logger = LoggerFactory.getLogger(CounterController.class);
    Stage stage;

    /**
     * 是否跳转登录页面flag
     */
    BooleanProperty jumpToLandFlag = new SimpleBooleanProperty(false);


    /**
     * 图片所在文件夹
     */
    private File imgDir;

    /**
     * Excel保存文件夹
     */
    private File excelDir;

    @FXML
    VBox panel_choose;
    @FXML
    VBox panel_progress;
    @FXML
    VBox panel_success;
    @FXML
    VBox panel_fail;

    /**
     * 显示图片文件夹路径
     */
    @FXML
    Text text_img_dir;

    /**
     * 显示Excel文件夹路径
     * 未选择显示:未选择任何文件;已选择显示:选择文件夹的全路径
     */
    @FXML
    Text text_excel_dir;

    /**
     * 统计进度百分比提示内容
     */
    @FXML
    Text text_progress_info;

    /**
     * 统计完毕提示内容
     */
    @FXML
    Text text_success_info;

    /**
     * 统计进度条
     */
    @FXML
    ProgressBar progress_bar;


    @FXML
    public void initialize() {
        this.stage = Main.stage;
        text_img_dir.setText("未选择");
        text_excel_dir.setText("未选择");
        showPanel(panel_choose.getId());
    }

    /**
     * 数据重置
     */
    private void resetValues() {
        imgDir = null;
        excelDir = null;
        text_img_dir.setText("未选择");
        text_excel_dir.setText("未选择");
    }

    /**
     * 开始统计
     *
     * @param mouseEvent
     */
    @FXML
    public void startCollect(MouseEvent mouseEvent) {
        MouseButton button = mouseEvent.getButton();
        if (MouseButton.PRIMARY.equals(button)) {
            logger.info(button.name() + "....");
            if (null == imgDir) {
                return;
            }
            logger.info("dir to upload: " + imgDir.getAbsolutePath());

            //显示上传中界面
            showPanel(panel_progress.getId());

            CollectWorkerTask workerTask = CollectWorkerTask.with(imgDir, excelDir);
            workerTask.messageProperty()
                      .addListener((observable, oldVal, newVal) -> {
                          if (CollectWorkerTask.PROGRESS_MSG_ERROR.equals(newVal)) {
                              logger.info("upload progress msg..." + newVal);
                              //显示失败页面
                              showPanel(panel_fail.getId());
                              text_progress_info.setText(0 + "%");
                          } else if (CollectWorkerTask.PROGRESS_MSG_COMPLETE.equals(newVal)) {
                              logger.info("upload progress msg..." + newVal);
                              //显示成功页面
                              showPanel(panel_success.getId());
                              int size = FileUtils.getJpgFilesInDir(imgDir).size();
                              text_success_info.setText("图片总数: " + size);
                          } else {
                              logger.info("upload progress msg..." + newVal);
                              text_progress_info.setText(newVal + "%");
                          }
                      });

            progress_bar.progressProperty()
                        .bind(workerTask.progressProperty());

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(workerTask);

        }
    }

    /**
     * 显示对应界面
     *
     * @param panelId 待显示界面的id
     */
    private void showPanel(String panelId) {
        panel_choose.setVisible(panel_choose.getId().equals(panelId));
        panel_progress.setVisible(panel_progress.getId().equals(panelId));
        panel_fail.setVisible(panel_fail.getId().equals(panelId));
        panel_success.setVisible(panel_success.getId().equals(panelId));
    }


    /**
     * 取消上传,上传成功确认按钮与上传失败取消按钮亦调用此方法
     *
     * @param mouseEvent
     */
    @FXML
    public void cancelCollect(MouseEvent mouseEvent) {
        MouseButton button = mouseEvent.getButton();
        if (MouseButton.PRIMARY.equals(button)) {
            logger.info(button.name() + "....");
            resetValues();
            stage.hide();
            System.exit(0);
        }
    }

    /**
     * 选择图片文件夹
     *
     * @param mouseEvent
     */
    @FXML
    public void chooseImgDir(MouseEvent mouseEvent) {
        MouseButton button = mouseEvent.getButton();
        if (MouseButton.PRIMARY.equals(button)) {
            logger.info(button.name() + "....");
            DirectoryChooser directoryChooser = new DirectoryChooser();
            imgDir = directoryChooser.showDialog(stage);
            if (null != imgDir) {
                logger.info("choose imgDir is: " + imgDir.getAbsolutePath());
                text_img_dir.setText(imgDir.getAbsolutePath());
            }
        }
    }

    /**
     * 选择Excel保存文件夹
     *
     * @param mouseEvent
     */
    @FXML
    public void chooseExcelDir(MouseEvent mouseEvent) {
        MouseButton button = mouseEvent.getButton();
        if (MouseButton.PRIMARY.equals(button)) {
            logger.info(button.name() + "....");
            DirectoryChooser directoryChooser = new DirectoryChooser();
            excelDir = directoryChooser.showDialog(stage);
            if (null != excelDir) {
                logger.info("choose imgDir is: " + excelDir.getAbsolutePath());
                text_excel_dir.setText(excelDir.getAbsolutePath());
            }
        }
    }

    /**
     * 上传失败重试
     *
     * @param mouseEvent
     */
    @FXML
    public void retry(MouseEvent mouseEvent) {
        logger.info("into retry...");
        //显示上传中页面
        showPanel(panel_progress.getId());
        startCollect(mouseEvent);
    }


}
