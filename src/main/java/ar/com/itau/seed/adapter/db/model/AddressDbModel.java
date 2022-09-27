package ar.com.itau.seed.adapter.db.model;

import ar.com.itau.seed.domain.Address;
import lombok.Data;

@Data
public class AddressDbModel {

    public static AddressDbModel from(final Address domain) {
        final AddressDbModel model = new AddressDbModel();
        model.setStreet(domain.getStreet());
        model.setNumber(domain.getNumber());
        model.setFloor(domain.getFloor());
        model.setApartment(domain.getApartment());
        model.setZipCode(domain.getZipCode());
        model.setLandlinePrefix(domain.getLandlinePrefix());
        model.setLandlineNumber(domain.getLandlineNumber());
        return model;
    }

    private String street;
    private String number;
    private String floor;
    private String apartment;
    private String zipCode;
    private Integer landlinePrefix;
    private Long landlineNumber;

    public Address toDomain() {
        return Address.builder().build();
    }

}
