package com.algorithm.project.service;

import com.algorithm.project.domain.CoffeeShop;
import com.algorithm.project.repository.CoffeeShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ForestService {

    private final CoffeeShopRepository coffeeShopRepository;

    private static final int MIN_CLUSTER_SIZE = 3;


    /**
     * 카페 데이터를 불러온 후, DFS 알고리즘을 이용해 연결 요소 분석을 수행합니다.
     *
     * @param distanceThresholdMeters 군집 형성 거리 (임계값)
     * @return 카페 군집
     */
    @Transactional(readOnly = true)
    public List<List<CoffeeShop>> analyzeClusters(double distanceThresholdMeters) {
        List<CoffeeShop> allShops = coffeeShopRepository.findAll();
        List<List<CoffeeShop>> clusters = new ArrayList<>();
        Set<Long> visited = new HashSet<>();

        // 모든 노드에 대해 방문하지 않았다면 새로운 군집 탐색 시작
        for (CoffeeShop shop : allShops) {
            if (!visited.contains(shop.getId())) {
                List<CoffeeShop> component = new ArrayList<>();
                dfs(shop, allShops, visited, component, distanceThresholdMeters);
                if (component.size() >= MIN_CLUSTER_SIZE) {
                    clusters.add(component);
                }
            }
        }
        // 군집 크기순으로 내림차순 정렬
        clusters.sort((c1, c2)
                -> Integer.compare(c2.size(), c1.size()));
        return clusters;
    }


    /**
     * DFS 알고리즘을 이용해 연결된 모든 노드를 찾습니다.
     *
     * @param current   탐색 시작할 현재 노드
     * @param allShops  전체 탐색 범위
     * @param visited   방문 여부
     * @param component 연결 요소
     * @param threshold 군집 형성 거리 (임계값)
     */
    private void dfs(CoffeeShop current, List<CoffeeShop> allShops,
                     Set<Long> visited, List<CoffeeShop> component, double threshold
    ) {
        visited.add(current.getId());
        component.add(current);

        for (CoffeeShop other : allShops) {
            if (!visited.contains(other.getId())) {
                double distance = calculateDistance(
                        current.getLatitude(), current.getLongitude(),
                        other.getLatitude(), other.getLongitude()
                );
                if (distance <= threshold) {
                    dfs(other, allShops, visited, component, threshold);
                }
            }
        }
    }


    /**
     * 하버사인 공식을 이용해 두 지점 간 거리를 계산합니다.
     *
     * @param lat1 위도1
     * @param lon1 경도1
     * @param lat2 위도2
     * @param lon2 경도2
     * @return 거리(m)
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반지름 (km)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000; // 미터 단위로 변환
    }
}
