package middlegen.validator.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import middlegen.FileProducer;
import middlegen.Util;
import middlegen.validator.ErrorMessage;
import middlegen.validator.ValidatorAdapter;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;

/**
 * sqlmap配置校验器
 * 该验证器针对生成的文件和即将被替换的原文件做比较，
 * 如果原文件中定义的resource条数大于生成文件的条数，验证失败
 * @author yong.liuhy
 *
 */
public class SqlMapConfigValidator extends ValidatorAdapter {

    public List<ErrorMessage> validateAfterGenerate(File generatedFile, File replacedFile,
                                                    FileProducer fileProcucer) {

        List<ErrorMessage> result = new ArrayList<ErrorMessage>();

        List<SqlMapResource> generatedResources = new SqlMapResourcesHandler().parse(generatedFile);
        List<SqlMapResource> replacedResources = new SqlMapResourcesHandler().parse(replacedFile);

        String temFileName = StringUtils.substringAfterLast(fileProcucer.getTemplate().getFile(),
            "/");
        String replacedFileName = fileProcucer.getDestinationFileName();

        if (replacedResources != null && replacedResources.size() > 0) {
            if (generatedResources == null || generatedResources.size() <= 0) {
                ErrorMessage message = new ErrorMessage();
                message.setMessage("模板文件:" + temFileName + "生成的sqlmap定义为0条，原文件:" + replacedFileName
                                   + "的sqlmap定义为" + replacedResources.size() + "条，请检查模板文件定义是否正确。");
                result.add(message);
                return result;
            }

            for (SqlMapResource resource : replacedResources) {
                if (!generatedResources.contains(resource)) {
                    ErrorMessage message = new ErrorMessage();
                    message.setMessage(resource + "在：" + replacedFileName + "中有定义,但在模板文件："
                                       + temFileName + "文件中却没有定义，你可能忘记了在模板文件中定义该resource 。");
                    result.add(message);
                }
            }
        }

        return result;
    }

    private static class SqlMapResourcesHandler {

        private final List<SqlMapResource> resources;

        public void addResource(String str) {
            this.resources.add(new SqlMapResource(str));
        }

        public List<SqlMapResource> getResources() {
            return resources;
        }

        public SqlMapResourcesHandler() {
            this.resources = new ArrayList<SqlMapResource>();
        }

        public List<SqlMapResource> parse(File sqlMapFile) {
            try {
                Digester digester = new Digester();
                digester.setValidating(false);
                digester.push(this);
                digester.addCallMethod("sqlMapConfig/sqlMap", "addResource", 1,
                    new String[] { "java.lang.String" });
                digester.addCallParam("sqlMapConfig/sqlMap", 0, "resource");

                String str = Util.trimDocType(sqlMapFile);

                digester.parse(new StringReader(str));

                return this.getResources();

            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalStateException(sqlMapFile.getAbsolutePath() + "解析出错，请检查格式是否正确");
            }
        }
    }

    public static class SqlMapResource {

        public SqlMapResource(String resource) {
            this.resource = resource;
        }

        private String resource;

        public String getResource() {
            return resource;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }

        public String toString() {
            return "[resource=" + this.resource + "]";
        }

        public boolean equals(Object obj) {
            return StringUtils.equalsIgnoreCase(((SqlMapResource) obj).resource, resource);
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

        URL url = new URL(
            "file:D:/projects/yong_liuhy_CP-mipgw-unittest-080-20091223_intg/vobs/mipgw/mipgw-dalgen/templates/beans-dal-apayfund-dao.vm");

        System.out.println(StringUtils.substringAfterLast(url.getFile(), "/"));
    }
}
