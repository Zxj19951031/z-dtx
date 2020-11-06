package org.zipper.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author zhuxj
 * @since 2020/11/5
 */
@RestController
@RequestMapping(value = "authenticate")
public class AuthenticationController {

    @GetMapping(value = "user")
    public Principal user(Principal principal){
        return principal;
    }
}
