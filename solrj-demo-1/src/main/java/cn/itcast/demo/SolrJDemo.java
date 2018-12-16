package cn.itcast.demo;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * solrj
 */
public class SolrJDemo {

    @Test
    public void addIndex() throws IOException, SolrServerException {

        SolrServer solrServer = new HttpSolrServer("http://localhost:8088/solr/item_collection");

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id",1);
        doc.addField("item_brand","华为");
        doc.addField("item_title","华为 p20");


        solrServer.add(doc);
        solrServer.commit();

    }



    @Test
    public void addIndexs() throws IOException, SolrServerException {

        SolrServer solrServer = new HttpSolrServer("http://localhost:8088/solr/item_collection");
        List<SolrInputDocument> docList = new ArrayList<SolrInputDocument>();
        for(int i =0 ; i < 100 ; i++) {
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", 2+i);
            doc.addField("item_brand", "华为");
            doc.addField("item_price",10000+i);
            doc.addField("item_title", "华为 p20" + i);

            docList.add(doc);
        }

        solrServer.add(docList);
        solrServer.commit();

    }


    /**
     *
     * 更新索引
     *
     * add() 既可以添加, 又可以更新
     *      根据ID进行判定, 如果ID不存在, 则添加
     *                    如果ID 存在, 则更新(先查询, 再删除, 再添加)
     *
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void updateIndex() throws IOException, SolrServerException {

        SolrServer solrServer = new HttpSolrServer("http://localhost:8088/solr/item_collection");

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id",10000);
        doc.addField("item_brand","小米");

        solrServer.add(doc);
        solrServer.commit();

    }


    /**
     * 查询索引
     */
    @Test
    public void searchIndex() throws IOException, SolrServerException {

        SolrServer solrServer = new HttpSolrServer("http://localhost:8088/solr/item_collection");

        //SolrQuery就是用来组装查询条件
        SolrQuery query = new SolrQuery("item_title:华为");
        query.add("fq","item_price:[10000 TO 10010]");
        query.addSort("item_price", SolrQuery.ORDER.desc);

        //分页
        query.setStart(0);
        query.setRows(5);

        //query.set("fl","item_title");

        query.setHighlight(true);
        query.addHighlightField("item_title");
        query.setHighlightSimplePre("<font color='red'>");
        query.setHighlightSimplePost("</font>");


        QueryResponse response = solrServer.query(query);

        SolrDocumentList solrDocumentList = response.getResults();
        long numFound = solrDocumentList.getNumFound();
        System.out.println("符合条件的记录总数 : " + numFound);

        //获取高亮结果集合
        Map<String, Map<String, List<String>>> map = response.getHighlighting();

        for (SolrDocument document : solrDocumentList) {
           String id =  document.get("id").toString();
            System.out.println(id);
            System.out.println(document.get("item_title").toString());
            System.out.println();

            if(map != null){
                Map<String, List<String>> map1 = map.get(id);
                if(map1 !=null){
                    List<String> list = map1.get("item_title");
                    if(list !=null && list.size()>0){
                        System.out.println("item_title 高亮显示后结果 : " + list.get(0));
                    }
                }
            }

        }

    }

    /**
     * 删除索引
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void deleteIndex() throws IOException, SolrServerException {

        SolrServer solrServer = new HttpSolrServer("http://localhost:8088/solr/item_collection");

//        solrServer.deleteById("1"); //根据主键删除

        solrServer.deleteByQuery("*:*");

        solrServer.commit();

    }



}
