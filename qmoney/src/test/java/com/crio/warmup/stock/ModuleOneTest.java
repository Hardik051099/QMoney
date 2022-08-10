
package com.crio.warmup.stock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ModuleOneTest {

  @Test
  void mainReadFile() throws Exception {
    //given
    String filename = "assessments/trades.json";
    List<String> expected = new ArrayList<>();
    Collections.addAll(expected, new String[]{"MSFT", "CSCO", "CTS"});
    //when
    List<String> results = PortfolioManagerApplication
        .mainReadFile(new String[]{filename});

    //then
    Assertions.assertEquals(expected, results);
  }

  @Test
  void mainReadFileEdgecase() throws Exception {
    //given
    String filename = "assessments/empty.json";
    List<String> expected = new ArrayList<>();

    //when
    List<String> results = PortfolioManagerApplication
        .mainReadFile(new String[]{filename});

    //then
    Assertions.assertEquals(expected, results);
  }

}
