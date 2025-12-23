package com.mms.base.feign.usercenter.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserAuthorityDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Set<String> roles = new HashSet<>();

    private Set<String> permissions = new HashSet<>();
}

