package com.example.fiscos.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fiscos.dto.qrCode.AddQRCodeDTO;
import com.example.fiscos.dto.rawProduct.RawProductDTO;
import com.example.fiscos.dto.supplier.SupplierDTO;
import com.example.fiscos.mapper.RawProductMapper;
import com.example.fiscos.mapper.SupplierMapper;
import com.example.fiscos.model.Classification;
import com.example.fiscos.model.ProductClassification;
import com.example.fiscos.repository.ClassificationRepository;
import com.example.fiscos.repository.ProcessedProductRepository;
import com.example.fiscos.repository.ProductClassificationRepository;
import com.example.fiscos.repository.RawProductRepository;
import com.example.fiscos.repository.SupplierRepository;
import com.example.fiscos.service.NFeService;
import com.example.fiscos.service.QrCodeService;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/qrcode")
public class QrCodeController {

    private final QrCodeService qrCodeService;
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final RawProductRepository rawProductRepository;
    private final RawProductMapper rawProductMapper;
    private final NFeService nfeService;
    private final ProcessedProductRepository processedProductRepository;
    private final ProductClassificationRepository productClassificationRepository;

    QrCodeController(QrCodeService qrCodeService, SupplierRepository supplierRepository,
            SupplierMapper supplierMapper, RawProductRepository rawProductRepository,
            RawProductMapper rawProductMapper,
            NFeService nfeService, ProcessedProductRepository processedProductRepository,
            ProductClassificationRepository productClassificationRepository,
            ClassificationRepository classificationRepository) {
        this.qrCodeService = qrCodeService;
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
        this.rawProductRepository = rawProductRepository;
        this.rawProductMapper = rawProductMapper;
        this.nfeService = nfeService;
        this.processedProductRepository = processedProductRepository;
        this.productClassificationRepository = productClassificationRepository;
    }

    @GetMapping
    public ResponseEntity<?> getInvoices(@RequestParam Long userId) {
        return ResponseEntity.ok().body(
                qrCodeService.getAllQrCodes().stream()
                        .map(qrCode -> qrCode.getLink())
                        .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<?> addLinks(@RequestBody @Valid AddQRCodeDTO dto) {
        nfeService.saveAll(dto.getUserId(), dto.getLinks());
        return ResponseEntity.status(201).body(dto);
    }

    @GetMapping("/supplier")
    public List<SupplierDTO> getMethodName() {
        return supplierRepository.findAll().stream()
                .map(
                        supplier -> supplierMapper.toDto(
                                supplier))
                .toList();
    }

    @GetMapping("/rawProduct")
    public List<RawProductDTO> getRawProduct() {
        return rawProductRepository.findAll().stream().map(
                rawProduct -> rawProductMapper.toDto(rawProduct)

        ).toList();
    }

    @GetMapping("/processedProduct")
    public String getProcessedProduct() {
        return processedProductRepository.findAll().stream()
                .map(processedProduct -> processedProduct.getName())
                .collect(Collectors.joining(", "));
    }

    @GetMapping("/productClassification")
    public String getProductClassification() {
        return productClassificationRepository.findAll().stream()
                .map(p -> p.getClassification().getId() + " - " + p.getClassification().getName() + " - " + p.getProcessedProduct().getName() + " - " + p.getProcessedProduct().getId())
                .collect(Collectors.joining(", "));
    }

    @GetMapping("/qrcode")
    public String getInvoice() {
        return qrCodeService.getAllQrCodes().stream()
                .filter(qr -> qr.getInvoice() != null) // segurança extra
                .flatMap(qr -> qr.getInvoice().getProductInvoices().stream())
                .filter(pi -> pi.getProcessedProduct() != null)
                .map(pi -> pi.getProcessedProduct().getName() + " - " + pi.getQuantity())
                .collect(Collectors.joining(", "));
    }

}
