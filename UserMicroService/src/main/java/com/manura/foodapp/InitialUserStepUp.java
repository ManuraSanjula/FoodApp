package com.manura.foodapp;
// package com.manura.app;

// import com.manura.app.entity.AuthorityEntity;
// import com.manura.app.entity.RoleEntity;
// import com.manura.app.entity.UserEntity;
// import com.manura.app.repository.AuthorityRepo;
// import com.manura.app.repository.RoleRepo;
// import com.manura.app.repository.UserRepo;
// import com.manura.app.shared.Utils.Utils;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.context.event.ApplicationReadyEvent;
// import org.springframework.context.event.EventListener;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.stereotype.Component;

// import javax.transaction.Transactional;
// import java.util.Arrays;
// import java.util.Collection;

// @Component
// public class InitialUserStepUp {

//    @Autowired
//    AuthorityRepo authorityRepo;

//    @Autowired
//    RoleRepo roleRepo;

//    @Autowired
//    Utils utils;

//    @Autowired
//    BCryptPasswordEncoder passwordEncoder;

//    @Autowired
//    UserRepo userRepo;

//    @EventListener
//    @Transactional
//    public void onApplicationEvent(ApplicationReadyEvent ev){
//        AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
//        AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
//        AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");
//        AuthorityEntity updateAuthority = createAuthority("UPDATE_AUTHORITY");

//        RoleEntity ROLE_USER = createRole("ROLE_USER", Arrays.asList(readAuthority,writeAuthority,updateAuthority,deleteAuthority));
//        RoleEntity ROLE_CHEF = createRole("ROLE_CHEF", Arrays.asList(readAuthority,writeAuthority,updateAuthority));
//        RoleEntity ROLE_ADMIN = createRole("ROLE_ADMIN", Arrays.asList(readAuthority,writeAuthority,updateAuthority,deleteAuthority));

//        if(ROLE_ADMIN == null)
//             return;

//        UserEntity userEntity = new UserEntity();
//        userEntity.setAddress("Panadura");
//        userEntity.setFirstName("Manura");
//        userEntity.setLastName("Sanjula");
//        userEntity.setEmail("w.m.manurasanjula12345@gmail.com");
//        userEntity.setEmailVerify(true);
//        userEntity.setPassword(passwordEncoder.encode("123456789"));
//        userEntity.setRole(Arrays.asList(ROLE_ADMIN));
//        userEntity.setPublicId(utils.generateUserId(30));

//        userRepo.save(userEntity);
//    }

//    @Transactional
//    AuthorityEntity createAuthority(String name){
//        AuthorityEntity authority  = authorityRepo.findByName(name);
//        if(authority == null){
//            authority = new AuthorityEntity(name);
//            authorityRepo.save(authority);
//        }
//        return authority;
//    }

//    @Transactional
//    RoleEntity createRole(String name, Collection<AuthorityEntity> authorities){
//        RoleEntity role = roleRepo.findByRole(name);
//        if(role == null){
//            role = new RoleEntity(name);
//            role.setAuthorities(authorities);
//            roleRepo.save(role);
//        }
//        return role;
//    }

// }
