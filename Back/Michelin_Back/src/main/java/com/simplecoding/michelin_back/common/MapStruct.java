package com.simplecoding.michelin_back.common;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct 공통 매퍼
 * 각 도메인별 매핑은 해당 파트 담당자가 @Mapping 추가
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MapStruct {
    // 도메인별 매핑 메서드는 각 파트에서 추가
}
