package com.example.fiscos.dto.qrCode;

import java.util.List;

import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AddQRCodeDTO {

    @NotEmpty(message = "A lista de links não pode estar vazia.")
    @JsonProperty("links")
    private List<@URL(message = "O link fornecido não é uma URL válida.") String> links;
}
