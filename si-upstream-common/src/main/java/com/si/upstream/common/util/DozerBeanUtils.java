package com.si.upstream.common.util;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

import java.util.Optional;

/**
 * @author sunxibin
 */
public class DozerBeanUtils {
    private static final Mapper MAPPER = new DozerBeanMapper();

    private static Mapper getDozerBeanMapper() {
        return MAPPER;
    }

    /**
     * map
     *
     * @param source
     * @param destinationClass
     * @param <T>
     * @return
     */
    public static <T> T map(Object source, Class<T> destinationClass) {
        return Optional.ofNullable(source).map(s -> getDozerBeanMapper().map(source, destinationClass)).orElse(null);
    }

    /**
     * map
     *
     * @param source
     * @param destination
     */
    public static void map(Object source, Object destination) {
        getDozerBeanMapper().map(source, destination);
    }
}
