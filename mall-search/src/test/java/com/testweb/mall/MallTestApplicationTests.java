package com.testweb.mall;

import com.testweb.mall.dao.EsProductDao;
import com.testweb.mall.domain.EsProduct;
import com.testweb.mall.repository.EsProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.util.List;


@SpringBootTest
@EnableElasticsearchRepositories(basePackages = "com.testweb.mall.repository")
class MallTestApplicationTests {
    @Autowired(required = false)
    private EsProductDao productDao;

    @Autowired
    private EsProductRepository productRepository;
  @Test
  void contextLoads() {
//        List<EsProduct> esProductList = productDao.getAllEsProductList(null);
//      Iterable<EsProduct> esProductIterable = productRepository.saveAll(esProductList);
        System.out.println(productDao);
    }

}