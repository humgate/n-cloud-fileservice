package com.humga.cloudservice.security;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Черный список токенов, реализующий автоматическое удаление элементов при наступлении момента времени, указанного
 * в элементе списка.
 *
 */
public interface AutoExpiringBlackList {
    /**
     * Добавляет элемент в список
     *
     * @param token - токен
     * @param info - объект, содержащий дополнительную информацию о записи токена в черном списке
     * @param expirationTime - момент времени окончания действия токена
     */
    void add(String token, Object info, LocalDateTime expirationTime);

    /**
     * Проверяет, есть ли переданный токен в черном списке
     *
     * @param token - токен
     * @return - true если есть, иначе false
     */
    boolean contains(String token);

    /**
     * Возвращает информацию о записи черного списка
     *
     * @param token - токен
     * @return - объект с информацией,
     */
    Object getInfo(String token);

    /**
     * Возвращает полный черный список на момент выполнения метода
     *
     * @return - черный список
     */
    Map<String, Object> getAll();
}
