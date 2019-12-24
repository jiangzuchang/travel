package cn.itcast.travel.dao.impl;

import cn.itcast.travel.domain.Seller;
import cn.itcast.travel.util.UtilsToDruid;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.Assert.*;

public class DaoImplTest {

    @Test
    public void findSeller() {
        try {
            String sql = "SELECT * FROM tab_seller " +
                    "LEFT OUTER JOIN tab_route ON tab_route.sid = tab_seller.sid WHERE rid = 1";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(UtilsToDruid.getDataSource());
            Seller seller = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<Seller>(Seller.class));
            System.out.println(seller);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("seller为空");
        }
    }
}