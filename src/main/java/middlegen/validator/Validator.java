package middlegen.validator;

import java.io.File;
import java.util.List;

import middlegen.FileProducer;

/**
 * 验证器接口
 */
public interface Validator {
    /**
     * 生成后验证，如果返回的错误列表不为空，将终止整个dalgen生成动作
     * @param generatedFile 将要生成的文件
     * @param replacedFile 将被替换的原文件
     * @param fileProcucer 
     * @return 
     */
    public List<ErrorMessage> validateAfterGenerate(File generatedFile, File replacedFile,
                                                    FileProducer fileProcucer);

    /**
     * 生成前验证， 如果返回的错误消息列表不为空，将终止整个dalgen生成
     */
    public List<ErrorMessage> validateBeforeGenerate(FileProducer fileProcucer);

}
