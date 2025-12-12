package com.algorithm.project.infrastructure;

import com.algorithm.project.domain.CoffeeShop;
import com.algorithm.project.repository.CoffeeShopRepository;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CoffeeShopRepository coffeeShopRepository;

    /**
     * 공공데이터포털의 [소상공인시장진흥공단 상가정보] 중
     * 카페 데이터만 필터링된 data.csv를 읽어 인메모리 DB에 적재합니다.
     *
     * @param args incoming main method arguments
     * @throws Exception override
     */
    @Override
    public void run(String... args) throws Exception {
        ClassPathResource resource = new ClassPathResource("data.csv");

        try (CSVReader reader = new CSVReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
        ) {
            List<String[]> rows = reader.readAll();

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                // 상호명(0), 도로명주소(1), 경도(2), 위도(3)
                try {
                    CoffeeShop shop = CoffeeShop.builder()
                            .name(row[0])
                            .address(row[1])
                            .longitude(Double.parseDouble(row[2]))
                            .latitude(Double.parseDouble(row[3]))
                            .build();
                    coffeeShopRepository.save(shop);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    log.warn("데이터 파싱 실패 (row {}): {}", i, e.getMessage());
                }
            }
            log.info("데이터 초기화 완료. 총 {} 개의 커피숍 데이터 로드됨.", coffeeShopRepository.count());
        }
    }
}
