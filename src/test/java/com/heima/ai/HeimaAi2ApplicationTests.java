package com.heima.ai;

import com.heima.ai.utils.VectorDistanceUtils;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.Arrays;
import java.util.List;

@MapperScan("com.heima.ai.mapper")
@SpringBootTest
class HeimaAi2ApplicationTests {
    @Autowired
    private OpenAiEmbeddingModel openAiEmbeddingModel;
    @Autowired
    private VectorStore vectorStore;

    @Test
    public void testVectorStore() {
        Resource resource = new ClassPathResource("中二知识笔记.pdf");
        //创建pdf读取器
        PagePdfDocumentReader  reader = new PagePdfDocumentReader(
                resource, PdfDocumentReaderConfig.builder()
                //配置格式化器
                .withPageExtractedTextFormatter(ExtractedTextFormatter.defaults())
                //配置按多少页进行拆分
                .withPagesPerDocument(1)
                .build()
        );
        //读取pdf文件，拆分为document
List<Document> documents = reader.read();
        //写入向量数据库
        vectorStore.add(documents);
        //模拟搜索
        //这个是自定义query条件
        SearchRequest request = SearchRequest.builder()
                .query("论语中教育的目的是什么")
                .topK(3)
                .similarityThreshold(0.6)
                .filterExpression("fIle_name == '中二知识笔记.pdf'")
                .build();
        List<Document> search = vectorStore.similaritySearch("论语中教育的目的是什么");
        if (search == null){
            System.out.println("没有找到任何内容");
            return;
        }
        //呈现搜索内容
        for (Document document : search) {
            System.out.println(document.getId());
            System.out.println(document.getScore());
            System.out.println(document.getText());
        }


}

    @Test
    void contextLoads() {
        float[] vector = openAiEmbeddingModel.embed("hello world");
        System.out.println(Arrays.toString(vector));
    }
    @Test
    public void testEmbedding() {
        // 1.测试数据
        // 1.1.用来查询的文本，国际冲突
        String query = "global conflicts";

        // 1.2.用来做比较的文本
        String[] texts = new String[]{
                "哈马斯称加沙下阶段停火谈判仍在进行 以方尚未做出承诺",
                "土耳其、芬兰、瑞典与北约代表将继续就瑞典“入约”问题进行谈判",
                "日本航空基地水井中检测出有机氟化物超标",
                "国家游泳中心（水立方）：恢复游泳、嬉水乐园等水上项目运营",
                "我国首次在空间站开展舱外辐射生物学暴露实验",
        };
        // 2.向量化
        // 2.1.先将查询文本向量化
        float[] queryVector = openAiEmbeddingModel.embed(query);

        // 2.2.再将比较文本向量化，放到一个数组
        List<float[]> textVectors = openAiEmbeddingModel.embed(Arrays.asList(texts));

        // 3.比较欧氏距离
        // 3.1.把查询文本自己与自己比较，肯定是相似度最高的
        System.out.println(VectorDistanceUtils.euclideanDistance(queryVector, queryVector));
        // 3.2.把查询文本与其它文本比较
        for (float[] textVector : textVectors) {
            System.out.println(VectorDistanceUtils.euclideanDistance(queryVector, textVector));
        }
        System.out.println("------------------");

        // 4.比较余弦距离
        // 4.1.把查询文本自己与自己比较，肯定是相似度最高的
        System.out.println(VectorDistanceUtils.cosineDistance(queryVector, queryVector));
        // 4.2.把查询文本与其它文本比较
        for (float[] textVector : textVectors) {
            System.out.println(VectorDistanceUtils.cosineDistance(queryVector, textVector));
        }
    }
}
