package tn.monetique.cardmanagment.service.Imp.BankManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tn.monetique.cardmanagment.Entities.ConfigBank.Bank;
import tn.monetique.cardmanagment.Entities.ConfigBank.AtmData;
import tn.monetique.cardmanagment.Entities.ConfigBank.EmvData;
import tn.monetique.cardmanagment.Entities.ConfigBank.POSPBFXD;
import tn.monetique.cardmanagment.Entities.ConfigBank.PosData;
import tn.monetique.cardmanagment.payload.request.ConfigureDataRequest;
import tn.monetique.cardmanagment.payload.response.ConfigureDataResponse;
import tn.monetique.cardmanagment.repository.Bank.BankRepository;
import tn.monetique.cardmanagment.repository.Configbankentitiesrepo.AtmDataRepository;
import tn.monetique.cardmanagment.repository.Configbankentitiesrepo.EmvDataRepository;
import tn.monetique.cardmanagment.repository.Configbankentitiesrepo.POSPBFXDRepository;
import tn.monetique.cardmanagment.repository.Configbankentitiesrepo.PosDataRepository;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IAtmDataService;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IEmvDataServices;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IPOSPBFXDServices;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IPosDataService;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IagenceService;
import tn.monetique.cardmanagment.service.Interface.BankConfig.Ibankservice;
import tn.monetique.cardmanagment.service.Interface.BankConfig.IbinService;

import java.util.List;
import java.util.Optional;

@Service
public class bankService implements Ibankservice {


    @Autowired
    private BankRepository bankRepository;
    @Autowired
    private IAtmDataService atmDataService;

    @Autowired
    private IEmvDataServices emvDataService;

    @Autowired
    private IPosDataService posDataService;
    @Autowired
    IagenceService iagenceService;
    @Autowired
    IbinService ibinService;

    @Autowired
    private IPOSPBFXDServices pospbfxdService;
    @Autowired
    private AtmDataRepository atmDataRepository;
    @Autowired
    private EmvDataRepository emvDataRepository;
    @Autowired
    private PosDataRepository posDataRepository;
    @Autowired
    POSPBFXDRepository pospbfxdRepository;


    @Override
    public Bank creatBank(Bank bank) {
        return bankRepository.save(bank);
    }

    @Override
    public Bank updateBank(Long bankId, Bank updatedBank) {
        Optional<Bank> bankOptional = bankRepository.findById(bankId);
        if (bankOptional.isPresent()) {
            Bank existingBank = bankOptional.get();

            existingBank.setBankName(updatedBank.getBankName());
            existingBank.setCountryCode(updatedBank.getCountryCode());
            existingBank.setBankIdCode(updatedBank.getBankIdCode());
            existingBank.setBankLocation(updatedBank.getBankLocation());
            existingBank.setContactEmail(updatedBank.getContactEmail());
            existingBank.setMainOfficeAddress(updatedBank.getMainOfficeAddress());

            return bankRepository.save(existingBank);
        } else {
            return null; // Bank not found
        }
    }


    @Override
    public List<Bank> getallbank() {
        return bankRepository.findAll();
    }

    @Override
    public Optional<Bank> getbankbyid(Long bankId) {
        return bankRepository.findById(bankId);
    }

    @Override
    public boolean deleteBank(Long bankId) {

        if (bankRepository.existsById(bankId)) {
            ibinService.binsofbank(bankId);
            iagenceService.agencyofbank(bankId);
            bankRepository.deleteById(bankId);
            return true;
        } else {
            return false; // Bank not found
        }
}


        @Override
        public ConfigureDataResponse configureData(ConfigureDataRequest request, Long BinId) {
            EmvData emvData = (EmvData) request.getEmvData();
            AtmData atmData = (AtmData) request.getAtmData();
            POSPBFXD pospbfxd = (POSPBFXD) request.getPospbfxd();
            PosData posData = (PosData) request.getPosData();

            ConfigureDataResponse configureDataResponse = new ConfigureDataResponse();

            Optional<AtmData> existingAtmData = atmDataRepository.findByBin_BinId(BinId);
            if (existingAtmData.isPresent()) {
                // Data exists, update it
                Long atmDataId = existingAtmData.get().getAtmDataId();
                AtmData updatedAtmData = atmDataService.updateAtmData(atmDataId, atmData);
                configureDataResponse.setConfiguredAtmData(updatedAtmData);
            } else {
                // Data doesn't exist, create it
                AtmData configuredAtmData = atmDataService.CreateAtmData(atmData, BinId);
                configureDataResponse.setConfiguredAtmData(configuredAtmData);
            }

            Optional<EmvData> existingEmvData = emvDataRepository.findByBin_BinId(BinId);
            if (existingEmvData.isPresent()) {
                // Data exists, update it
                Long emvDataId = existingEmvData.get().getEmvDataId();
                EmvData updatedEmvData = emvDataService.updateEmvData(emvDataId, emvData);
                configureDataResponse.setConfiguredEmvData(updatedEmvData);
            } else {
                // Data doesn't exist, create it
                EmvData configuredEmvData = emvDataService.CreateEmvData(emvData, BinId);
                configureDataResponse.setConfiguredEmvData(configuredEmvData);
            }

            Optional<PosData> existingPosData = posDataRepository.findByBin_BinId(BinId);
            if (existingPosData.isPresent()) {
                // Data exists, update it
                Long posDataId = existingPosData.get().getPosDataId();
                PosData updatedPosData = posDataService.updatePosData(posDataId, posData);
                configureDataResponse.setConfiguredPosData(updatedPosData);
            } else {
                // Data doesn't exist, create it
                PosData configuredPosData = posDataService.createPosData(posData, BinId);
                configureDataResponse.setConfiguredPosData(configuredPosData);
            }

            Optional<POSPBFXD> existingPosPbfXd = pospbfxdRepository.findByBin_BinId(BinId);
            if (existingPosPbfXd.isPresent()) {
                // Data exists, update it
                Long posPbfXdId = existingPosPbfXd.get().getPosPbfXdId();
                POSPBFXD updatedPosPbfXd = pospbfxdService.updatePOSPBFXD(posPbfXdId, pospbfxd);
                configureDataResponse.setConfiguredPOSPBFXD(updatedPosPbfXd);
            } else {
                // Data doesn't exist, create it
                POSPBFXD configuredPosPbfXd = pospbfxdService.createPOSPBFXD(pospbfxd, BinId);
                configureDataResponse.setConfiguredPOSPBFXD(configuredPosPbfXd);
            }

            return configureDataResponse;

        }
    private boolean bankHasAtmData(Long BinId) {
        Optional<AtmData> atmData = atmDataRepository.findByBin_BinId(BinId);
        return atmData.isPresent();
    }

    private boolean bankHasPosData(Long BinId) {
        Optional<PosData> posData = posDataRepository.findByBin_BinId(BinId);
        return posData.isPresent();
    }

    private boolean bankHasPOSPBFXD(Long BinId) {
        Optional<POSPBFXD> pospbfxd = pospbfxdRepository.findByBin_BinId(BinId);
        return pospbfxd.isPresent();
    }

    private boolean bankHasEmvDATA(Long BinId) {
        Optional<EmvData> emvData = emvDataRepository.findByBin_BinId(BinId);
        return emvData.isPresent();
    }

    }

