package com.opaigc.server.infrastructure.utils;


import com.opaigc.server.infrastructure.http.Page;

/**
 * @author: Runner.dada
 * @date: 2021/1/26
 * @description: 分页工具类，转换为自定义的类
 **/
public class PageUtil {

	/**
	 * 将Pageable转换为业务定义的Page格式
	 **/
	public static <T> Page<T> convert(org.springframework.data.domain.Page<T> page) {
		return Page.<T>builder()
			.content(page.getContent())
			.page(page.getNumber())
			.perPage(page.getSize())
			.totalElements((int) page.getTotalElements())
			.totalPages(page.getTotalPages()).build();
	}
}
