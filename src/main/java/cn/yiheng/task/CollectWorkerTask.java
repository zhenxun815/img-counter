package cn.yiheng.task;

import cn.yiheng.model.ImgInfo;
import cn.yiheng.utils.FileUtils;
import cn.yiheng.utils.ImgUtils;
import javafx.concurrent.Task;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Yiheng
 * @create 4/1/2019
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "with")
public class CollectWorkerTask extends Task {

    public static String PROGRESS_MSG_ERROR = "error";
    public static String PROGRESS_MSG_COMPLETE = "complete";

    Logger logger = LoggerFactory.getLogger(CollectWorkerTask.class);

    @NonNull
    File imgDir;
    @NonNull
    File excelDir;

    int total;
    AtomicInteger completeCount;

    @Override
    protected Object call() throws Exception {
        logger.info("start collect task...");
        List<ImgInfo> imgInfos = collectImgInfo(imgDir);
        logger.info("collect complete: " + imgInfos.size());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> submit = executor.submit(ExcelTask.with(imgInfos, excelDir));
        updateMessage(submit.get() ? PROGRESS_MSG_COMPLETE : PROGRESS_MSG_ERROR);
        return imgInfos;
    }


    private List<ImgInfo> collectImgInfo(File imgDir) {
        logger.info("into collectImgInfo...");
        List<File> filesInDir = FileUtils.getJpgFilesInDir(imgDir);
        total = filesInDir.size();
        logger.info("total file count is: " + total);
        completeCount = new AtomicInteger(0);

        ArrayList<ImgInfo> imgInfos = filesInDir.stream()
                                                .collect(ArrayList::new, (list, file) -> {
                                                    long length = file.length();
                                                    String name = file.getName();
                                                    Dimension resolution = ImgUtils.getResolution(file);
                                                    list.add(ImgInfo.of(length, name, resolution));
                                                    logger.info(" get im info with name: " + name + ", size: " + length + ", resolution: " + resolution);
                                                    updateProgress(completeCount.incrementAndGet(), total);

                                                    double progress = (completeCount.get() + 0D) / total * 100;
                                                    logger.info("complete count is: " + completeCount.get() + ", progress is: " + progress);
                                                    DecimalFormat decimalFormat = new DecimalFormat("#0.0");
                                                    String formatProgress = decimalFormat.format(progress);
                                                    updateMessage(formatProgress);
                                                }, ArrayList::addAll);

        return imgInfos;
    }
}
