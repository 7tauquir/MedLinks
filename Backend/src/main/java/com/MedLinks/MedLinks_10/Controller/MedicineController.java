package com.MedLinks.MedLinks_10.Controller;

import com.MedLinks.MedLinks_10.DTO.MedicineRequest;
import com.MedLinks.MedLinks_10.DTO.MedicineResponse;
import com.MedLinks.MedLinks_10.Service.MedicineService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(
            MedicineService medicineService
    ) {
        this.medicineService = medicineService;
    }


    // ==========================================
    // ADD MEDICINE
    // WHOLESALER ONLY
    // ==========================================

    @PostMapping
    public ResponseEntity<MedicineResponse> addMedicine(
            @Valid @RequestBody MedicineRequest request,
            @RequestParam String sellerEmail
    ) {

        MedicineResponse response =
                medicineService.addMedicine(
                        request,
                        sellerEmail
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // ==========================================
    // GET ALL MEDICINES
    // SHOP OWNER + WHOLESALER
    // ==========================================

    @GetMapping
    public ResponseEntity<List<MedicineResponse>> getAllMedicines() {

        return ResponseEntity.ok(
                medicineService.getAllMedicines()
        );
    }


    // ==========================================
    // GET MEDICINE BY ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<MedicineResponse> getMedicineById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                medicineService.getMedicineById(id)
        );
    }


    // ==========================================
    // SEARCH MEDICINES
    // ==========================================

    @GetMapping("/search")
    public ResponseEntity<List<MedicineResponse>> searchMedicines(
            @RequestParam String name
    ) {

        return ResponseEntity.ok(
                medicineService.searchMedicines(name)
        );
    }


    // ==========================================
    // FILTER BY CATEGORY
    // ==========================================

    @GetMapping("/category")
    public ResponseEntity<List<MedicineResponse>> getByCategory(
            @RequestParam String category
    ) {

        return ResponseEntity.ok(
                medicineService.getMedicinesByCategory(
                        category
                )
        );
    }


    // ==========================================
    // GET MEDICINES BY SELLER
    // WHOLESALER
    // ==========================================

    @GetMapping("/seller")
    public ResponseEntity<List<MedicineResponse>> getBySeller(
            @RequestParam String sellerEmail
    ) {

        return ResponseEntity.ok(
                medicineService.getMedicinesBySeller(
                        sellerEmail
                )
        );
    }


    // ==========================================
    // UPDATE MEDICINE
    // WHOLESALER ONLY
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<MedicineResponse> updateMedicine(
            @PathVariable Long id,
            @Valid @RequestBody MedicineRequest request,
            @RequestParam String sellerEmail
    ) {

        MedicineResponse response =
                medicineService.updateMedicine(
                        id,
                        request,
                        sellerEmail
                );

        return ResponseEntity.ok(response);
    }


    // ==========================================
    // DELETE MEDICINE
    // WHOLESALER ONLY
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMedicine(
            @PathVariable Long id,
            @RequestParam String sellerEmail
    ) {

        medicineService.deleteMedicine(
                id,
                sellerEmail
        );

        return ResponseEntity.ok(
                "Medicine deleted successfully"
        );
    }
}