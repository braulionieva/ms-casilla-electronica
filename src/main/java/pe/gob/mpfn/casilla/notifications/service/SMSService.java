package pe.gob.mpfn.casilla.notifications.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.mpfn.casilla.notifications.model.dto.GenerateSmsQueryResult;
import pe.gob.mpfn.casilla.notifications.model.dto.SmsGenerateCodeRequest;
import pe.gob.mpfn.casilla.notifications.repository.CelCodeSMSRepository;

import static pe.gob.mpfn.casilla.notifications.util.enums.AppParams.EMAIL_GENERATE_CODE_OPTION;
import static pe.gob.mpfn.casilla.notifications.util.enums.AppParams.SMS_VALIDATE_CODE_OPTION;

@Service
@Slf4j
public class SMSService {

    private final CelCodeSMSRepository celCodeSMSRepository;

    public SMSService(CelCodeSMSRepository celCodeSMSRepository) {
        this.celCodeSMSRepository = celCodeSMSRepository;
    }


    public GenerateSmsQueryResult generateCode(SmsGenerateCodeRequest codeRequest, String remoteAddr) {

        return celCodeSMSRepository.generateCode(codeRequest, remoteAddr, EMAIL_GENERATE_CODE_OPTION.getValue());

    }

    @Transactional
    public GenerateSmsQueryResult validate(SmsGenerateCodeRequest codeRequest, String remoteAddr) {

        return celCodeSMSRepository.generateCode(codeRequest, remoteAddr, SMS_VALIDATE_CODE_OPTION.getValue());

    }

}
