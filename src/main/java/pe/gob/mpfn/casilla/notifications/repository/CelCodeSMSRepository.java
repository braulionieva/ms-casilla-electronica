package pe.gob.mpfn.casilla.notifications.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import pe.gob.mpfn.casilla.notifications.exception.HttpStatusResponseException;
import pe.gob.mpfn.casilla.notifications.exception.InternalServerErrorException;
import pe.gob.mpfn.casilla.notifications.model.dto.GenerateSmsQueryResult;
import pe.gob.mpfn.casilla.notifications.model.dto.SmsGenerateCodeRequest;
import pe.gob.mpfn.casilla.notifications.util.enums.AppParams;
import pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle;

import java.sql.Types;
import java.util.Map;

import static pe.gob.mpfn.casilla.notifications.util.enums.AppParams.SP_ERROR_STATUS;
import static pe.gob.mpfn.casilla.notifications.util.enums.AppParams.SP_INTERNAL_ERROR;


@Repository
public class CelCodeSMSRepository {

    private static final Logger log = LoggerFactory.getLogger(CelCodeSMSRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public CelCodeSMSRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public GenerateSmsQueryResult generateCode(SmsGenerateCodeRequest codeRequest, String remoteAddr, String option) {

        var jdbcall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName(ObjetosOracle.SISCAS)
                .withCatalogName(ObjetosOracle.CAPK_AFIL_ONLINE)
                .withProcedureName(ObjetosOracle.CASP_GENERAR_CODIGO)
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("IN_OPCION", Types.VARCHAR),
                        new SqlParameter("IN_V_CORREO", Types.VARCHAR),
                        new SqlParameter("PI_V_CELULAR", Types.VARCHAR),
                        new SqlParameter("PI_V_NUMERO_DOC", Types.VARCHAR),
                        new SqlParameter("IN_CODIGO_GEN", Types.VARCHAR),
                        new SqlOutParameter("OUT_CODIGO_SMS", Types.VARCHAR),
                        new SqlOutParameter("PO_NOMBRE_COMPLETO", Types.VARCHAR),
                        new SqlOutParameter("PO_V_ERR_COD", Types.VARCHAR),
                        new SqlOutParameter("PO_V_ERR_MSG", Types.VARCHAR)
                );

        var maps = new MapSqlParameterSource("IN_OPCION", option)
                .addValue("IN_V_CORREO", codeRequest.email())
                .addValue("PI_V_CELULAR", codeRequest.celular())
                .addValue("PI_V_NUMERO_DOC", codeRequest.numeroDocumento());

        if (AppParams.SMS_VALIDATE_CODE_OPTION.getValue().equals(option)) {
            maps.addValue("IN_CODIGO_GEN", codeRequest.code());
        } else {
            maps.addValue("IN_CODIGO_GEN", null);
        }

        Map<String, Object> res = jdbcall.execute(maps);

        if (SP_INTERNAL_ERROR.getValue().equals(res.get("PO_V_ERR_COD").toString())) {
            log.error("Error en {}: {}", codeRequest.numeroDocumento(), res.get("PO_V_ERR_MSG"));
            throw new InternalServerErrorException();
        }

        if (SP_ERROR_STATUS.getValue().equals(res.get("PO_V_ERR_COD").toString())) {
            log.info("{}: {}", codeRequest.numeroDocumento(), res.get("PO_V_ERR_MSG"));
            throw new HttpStatusResponseException(HttpStatus.CONFLICT, res.get("PO_V_ERR_MSG").toString());
        }

        return new GenerateSmsQueryResult((String) res.get("OUT_CODIGO_SMS"), (String) res.get("OUT_CODIGO"), (String) res.get("OUT_MSG"), (String) res.get("PO_NOMBRE_COMPLETO"));
    }
}
