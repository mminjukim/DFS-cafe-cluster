package com.algorithm.project.repository;

import com.algorithm.project.domain.CoffeeShop;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoffeeShopRepository extends JpaRepository<@NonNull CoffeeShop, @NonNull Long> {

}
