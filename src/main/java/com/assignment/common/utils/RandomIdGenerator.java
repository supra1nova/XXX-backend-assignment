package com.assignment.common.utils;

import com.github.f4b6a3.tsid.TsidFactory;
import com.github.f4b6a3.uuid.UuidCreator;

public final class RandomIdGenerator {
    private static final TsidFactory TSID_FACTORY = TsidFactory.newInstance1024();

    private RandomIdGenerator() {}

    /**
     * 기본 UUID
     * 36자
     */
    public static String uuid() {
        return UuidCreator.getRandomBased().toString();
    }

    /**
     * epoch 정렬 UUID (시간 순 정렬 가능)
     * 36자
     */
    public static String epochUuid() {
        return UuidCreator.getTimeOrderedEpoch().toString();
    }

    /**
     * TSID (노드 기반 + 시간 순)
     * 16자
     * Time + Sequence + Node
     * 세 가지 요소로 이루어진 “시간 정렬 가능한 고유 ID”
     * 42 bits	timestamp (millisecond 단위)
     * 10 bits	node (서버나 인스턴스 구분용)
     * 12 bits	counter (같은 millisecond 내에서 증가)
     */
    public static String tsid() {
        return TSID_FACTORY.create().toString();
    }
}
