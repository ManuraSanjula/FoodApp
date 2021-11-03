package com.manura.foodapp.UserService.security;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.manura.foodapp.UserService.entity.UserEntity;
import com.manura.foodapp.UserService.repository.UserRepo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserUnlockService {

	private final UserRepo userRepository;
	@Scheduled(fixedRate = 300000)
	public void unlockAccounts() {
		List<UserEntity> lockedUsers = userRepository.findAllByAccountNonLockedAndLastModifiedDateIsBefore(false,
				Timestamp.valueOf(LocalDateTime.now().minusSeconds(30)));

		if (lockedUsers.size() > 0) {
			lockedUsers.forEach(user ->{
				 user.setAccountNonLocked(true);
			});

			userRepository.saveAll(lockedUsers);
		}
	}

}
