package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.OnboardingList;
import java.util.List;
import org.springframework.stereotype.Service;


@Service
public class OnboardingEmployeesServiceImpl implements OnboardingEmployeesService {
	private OnboardingEmployeesRepository onboardingEmployeesRepository;
	public OnboardingEmployeesServiceImpl(OnboardingEmployeesRepository onboardingEmployeesRepository) {
		super();
		this.onboardingEmployeesRepository = onboardingEmployeesRepository;
	}
	public List<OnboardingEmployees> getAllOnboardingEmployees() {
		return onboardingEmployeesRepository.findAll();
	}
	public OnboardingEmployees getOnboardingEmployeeById(Long id) {
		return onboardingEmployeesRepository.findById(id).get();
	}
	public OnboardingEmployees saveOnboardingEmployee(OnboardingEmployees onboardingEmployees) {
		return onboardingEmployeesRepository.save(onboardingEmployees);
	}
	public void deleteOnboardingEmployeeById(Long id) {
		onboardingEmployeesRepository.deleteById(id);
	}

	public OnboardingEmployees editOnboardingEmployee(OnboardingEmployees onboardingEmployees) {
		return onboardingEmployeesRepository.save(onboardingEmployees);
	}

}
