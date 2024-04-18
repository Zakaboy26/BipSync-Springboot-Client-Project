package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingList;

import org.springframework.stereotype.Service;

import java.util.List;
public interface OnboardingEmployeesService {
	List<OnboardingEmployees> getAllOnboardingEmployees();
	OnboardingEmployees editOnboardingEmployee(OnboardingEmployees onboardingEmployees);
	OnboardingEmployees getOnboardingEmployeeById(Long id);
	OnboardingEmployees saveOnboardingEmployee(OnboardingEmployees onboardingEmployees);
	void deleteOnboardingEmployeeById(Long id);
}
