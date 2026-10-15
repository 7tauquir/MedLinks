package com.MedLinks.MedLinks_10.ServiceImpl;

import com.MedLinks.MedLinks_10.DTO.MedicineRequest;
import com.MedLinks.MedLinks_10.DTO.MedicineResponse;
import com.MedLinks.MedLinks_10.Entities.Medicine;
import com.MedLinks.MedLinks_10.Entities.Role;
import com.MedLinks.MedLinks_10.Entities.User;
import com.MedLinks.MedLinks_10.Repository.MedicineRepository;
import com.MedLinks.MedLinks_10.Repository.UserRepository;
import com.MedLinks.MedLinks_10.Service.MedicineService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;
    private final UserRepository userRepository;

    public MedicineServiceImpl(
            MedicineRepository medicineRepository,
            UserRepository userRepository
    ) {
        this.medicineRepository = medicineRepository;
        this.userRepository = userRepository;
    }

    @Override
    public MedicineResponse addMedicine(
            MedicineRequest request,
            String sellerEmail
    ) {

        User seller = getWholesaler(sellerEmail);

        Medicine medicine = new Medicine();

        medicine.setName(request.getName());
        medicine.setCategory(request.getCategory());
        medicine.setManufacturer(request.getManufacturer());
        medicine.setPrice(request.getPrice());
        medicine.setStock(request.getStock());
        medicine.setBatchNumber(request.getBatchNumber());
        medicine.setExpiryDate(request.getExpiryDate());
        medicine.setPrescriptionRequired(
                request.getPrescriptionRequired()
        );

        medicine.setSeller(seller);

        Medicine savedMedicine = medicineRepository.save(medicine);

        return convertToResponse(savedMedicine);
    }

    @Override
    public MedicineResponse updateMedicine(
            Long medicineId,
            MedicineRequest request,
            String sellerEmail
    ) {

        User seller = getWholesaler(sellerEmail);

        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Medicine not found"
                        )
                );

        // Security check
        if (!medicine.getSeller().getId().equals(seller.getId())) {
            throw new RuntimeException(
                    "You cannot update this medicine"
            );
        }

        medicine.setName(request.getName());
        medicine.setCategory(request.getCategory());
        medicine.setManufacturer(request.getManufacturer());
        medicine.setPrice(request.getPrice());
        medicine.setStock(request.getStock());
        medicine.setBatchNumber(request.getBatchNumber());
        medicine.setExpiryDate(request.getExpiryDate());
        medicine.setPrescriptionRequired(
                request.getPrescriptionRequired()
        );

        Medicine updatedMedicine =
                medicineRepository.save(medicine);

        return convertToResponse(updatedMedicine);
    }

    @Override
    public void deleteMedicine(
            Long medicineId,
            String sellerEmail
    ) {

        User seller = getWholesaler(sellerEmail);

        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Medicine not found"
                        )
                );

        // Security check
        if (!medicine.getSeller().getId().equals(seller.getId())) {
            throw new RuntimeException(
                    "You cannot delete this medicine"
            );
        }

        medicineRepository.delete(medicine);
    }

    @Override
    public MedicineResponse getMedicineById(Long id) {

        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Medicine not found"
                        )
                );

        return convertToResponse(medicine);
    }

    @Override
    public List<MedicineResponse> getAllMedicines() {

        return medicineRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public List<MedicineResponse> getMedicinesBySeller(
            String sellerEmail
    ) {

        User seller = getWholesaler(sellerEmail);

        return medicineRepository.findBySeller(seller)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public List<MedicineResponse> searchMedicines(
            String name
    ) {

        return medicineRepository
                .findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public List<MedicineResponse> getMedicinesByCategory(
            String category
    ) {

        return medicineRepository
                .findByCategoryIgnoreCase(category)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // ===============================
    // Helper Methods
    // ===============================

    private User getWholesaler(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Wholesaler not found"
                        )
                );

        if (user.getRole() != Role.WHOLESALER) {
            throw new RuntimeException(
                    "Only wholesaler can manage medicines"
            );
        }

        return user;
    }


    private MedicineResponse convertToResponse(
            Medicine medicine
    ) {

        MedicineResponse response = new MedicineResponse();

        response.setId(medicine.getId());
        response.setName(medicine.getName());
        response.setCategory(medicine.getCategory());
        response.setManufacturer(medicine.getManufacturer());
        response.setPrice(medicine.getPrice());
        response.setStock(medicine.getStock());
        response.setBatchNumber(medicine.getBatchNumber());
        response.setExpiryDate(medicine.getExpiryDate());
        response.setPrescriptionRequired(
                medicine.getPrescriptionRequired()
        );

        if (medicine.getSeller() != null) {

            response.setSellerId(
                    medicine.getSeller().getId()
            );

            response.setSellerName(
                    medicine.getSeller().getName()
            );

            response.setSellerBusinessName(
                    medicine.getSeller().getBusinessName()
            );
        }

        return response;
    }
}