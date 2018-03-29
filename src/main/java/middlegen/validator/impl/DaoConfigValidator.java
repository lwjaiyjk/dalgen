package middlegen.validator.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import middlegen.FileProducer;
import middlegen.Util;
import middlegen.validator.ErrorMessage;
import middlegen.validator.ValidatorAdapter;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;

/**
 * DAO配置校验器
 * @author yong.liuhy
 *
 */
public class DaoConfigValidator extends ValidatorAdapter {
    public List<ErrorMessage> validateAfterGenerate(File generatedFile, File replacedFile,
                                                    FileProducer fileProcucer) {

        List<ErrorMessage> result = new ArrayList<ErrorMessage>();

        List<DaoConfig> generatedResources = new DaoConfigHandler().parse(generatedFile);
        List<DaoConfig> replacedResources = new DaoConfigHandler().parse(replacedFile);

        String temFileName = StringUtils.substringAfterLast(fileProcucer.getTemplate().getFile(),
            "/");
        String replacedFileName = fileProcucer.getDestinationFileName();

        if (replacedResources != null && replacedResources.size() > 0) {
            if (generatedResources == null || generatedResources.size() <= 0) {
                ErrorMessage message = new ErrorMessage();
                message.setMessage("模板文件:" + temFileName + "生成的bean定义为0条，原文件:" + replacedFileName
                                   + "的bean定义为" + replacedResources.size() + "条，请检查模板文件定义是否正确。");
                result.add(message);
                return result;
            }

            for (DaoConfig resource : replacedResources) {
                if (!generatedResources.contains(resource)) {
                    ErrorMessage message = new ErrorMessage();
                    message.setMessage(resource + "在:" + replacedFileName + "中有定义,在模板文件："
                                       + temFileName + "中却没有定义," + "你可能忘记了在模板文件中定义该bean。");
                    result.add(message);
                }
            }
        }

        return result;
    }

    public static class DaoConfigHandler {

        private final List<DaoConfig> configList;

        public void addConfig(String id, String className, String parent) {
            this.configList.add(new DaoConfig(id, className, parent));
        }

        public List<DaoConfig> getConfigList() {
            return configList;
        }

        public DaoConfigHandler() {
            this.configList = new ArrayList<DaoConfig>();
        }

        public List<DaoConfig> parse(File sqlMapFile) {
            try {
                Digester digester = new Digester();
                digester.setValidating(false);
                digester.push(this);

                digester.addCallMethod("beans/bean", "addConfig", 3, new String[] {
                        "java.lang.String", "java.lang.String", "java.lang.String" });
                digester.addCallParam("beans/bean", 0, "id");
                digester.addCallParam("beans/bean", 1, "class");
                digester.addCallParam("beans/bean", 2, "parent");

                String str = Util.trimDocType(sqlMapFile);
                digester.parse(new StringReader(str));
                return this.getConfigList();
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalStateException(sqlMapFile.getAbsolutePath() + "解析出错，请检查格式是否正确");
            }
        }
    }

    private static class DaoConfig {
        private String id;
        private String className;
        private String parent;

        public DaoConfig(String id, String className, String parent) {
            this.id = id;
            this.className = className;
            this.parent = parent;
        }

        public String toString() {
            return "[id=" + id + " class=" + className + " parent=" + parent + "]";
        }

        public boolean equals(Object obj) {
            DaoConfig config = (DaoConfig) obj;
            return StringUtils.equals(StringUtils.trim(config.id), StringUtils.trim(id))
                   && StringUtils.equals(StringUtils.trim(config.className), StringUtils
                       .trim(className))
                   && StringUtils.equals(StringUtils.trim(config.parent), StringUtils.trim(parent));
        }
    }

    public static void main(String args[]) throws Exception {
        FileInputStream in = new FileInputStream("D:\\test.xml");
        byte[] b = new byte[in.available()];
        in.read(b);
        in.close();

        File file = File.createTempFile("ccc", null);
        FileOutputStream out = new FileOutputStream(file);
        out.write(b);
        out.flush();
        out.close();

        System.out.println(new DaoConfigHandler().parse(file));
    }

}
