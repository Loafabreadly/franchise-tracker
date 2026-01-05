package com.github.loafabreadly.franchisetracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Contract {
    private double aav;
    private int termInYears;
    private int signingYear;
    private CONTRACT_CLAUSE clauses;
    private CONTRACT_STATUS status; // RFA/UFA
    private double signingBonus;
    private double performanceBonus;
    private boolean twoWay;
    
    /**
     * Gets the expiration year of the contract.
     */
    public int getExpirationYear() {
        return signingYear + termInYears;
    }
    
    /**
     * Calculates total contract value.
     */
    public double getTotalValue() {
        return aav * termInYears;
    }
    
    /**
     * Creates a new contract with basic parameters.
     */
    public static Contract createContract(double aav, int term, int signingYear, CONTRACT_STATUS status) {
        Contract contract = new Contract();
        contract.setAav(aav);
        contract.setTermInYears(term);
        contract.setSigningYear(signingYear);
        contract.setStatus(status);
        return contract;
    }
}
