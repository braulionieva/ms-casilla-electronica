package pe.gob.mpfn.casilla.notifications.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import pe.gob.mpfn.casilla.notifications.exception.HttpStatusResponseException;
import pe.gob.mpfn.casilla.notifications.exception.InternalServerErrorException;
import pe.gob.mpfn.casilla.notifications.model.dto.*;
import pe.gob.mpfn.casilla.notifications.model.user.UserDetail;
import pe.gob.mpfn.casilla.notifications.security.AuthenticationRequest;
import pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.sql.Types.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static pe.gob.mpfn.casilla.notifications.util.enums.AppParams.*;
import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AffiliateRepository {

    private final JdbcTemplate jdbcTemplate;

    public UpdatePasswordResponse updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withoutProcedureColumnMetaDataAccess()
                .withSchemaName(ObjetosOracle.SISCAS)
                .withCatalogName(ObjetosOracle.CAPK_AFILIADO)
                .withProcedureName(ObjetosOracle.CASP_ACTUALIZAR_PASSWORD)
                .declareParameters(
                        new SqlParameter("PI_USUARIO", VARCHAR),
                        new SqlParameter("PI_NEW_PW", VARCHAR),
                        new SqlParameter("PI_OLD_PW", VARCHAR),
                        new SqlParameter("PI_V_CO_V_VERIFICACION", VARCHAR),
                        new SqlParameter("PI_N_ID_TIPO_VERIFICACION", NUMERIC),
                        new SqlOutParameter(PO_V_ERR_COD, VARCHAR),
                        new SqlOutParameter(PO_V_ERR_MSG, VARCHAR)
                );

        SqlParameterSource objParametrosIN = new MapSqlParameterSource()
                .addValue("PI_USUARIO", updatePasswordRequest.userId())
                .addValue("PI_NEW_PW", updatePasswordRequest.pw())
                .addValue("PI_OLD_PW", updatePasswordRequest.oldPw())
                .addValue("PI_V_CO_V_VERIFICACION", updatePasswordRequest.codigoVerificacion())
           //     .addValue("PI_N_ID_TIPO_VERIFICACION", updatePasswordRequest.idTipoVerificacion())
                .addValue("PI_N_ID_TIPO_VERIFICACION", 1501);

        Map<String, Object> res = jdbcCall.execute(objParametrosIN);
        log.info(res.toString());
        if (SP_INTERNAL_ERROR.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.error("%s - user: %s".formatted(res.get(PO_V_ERR_MSG).toString(), updatePasswordRequest.userId()));
            throw new InternalServerErrorException();
        }
        if (SP_ERROR_STATUS.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.info("%s - user: %s".formatted(res.get(PO_V_ERR_MSG).toString(), updatePasswordRequest.userId()));
            throw new HttpStatusResponseException(BAD_REQUEST, res.get(PO_V_ERR_MSG).toString());
        }
        return new UpdatePasswordResponse(
                (String) res.get(PO_V_ERR_COD),
                (String) res.get(PO_V_ERR_MSG)
        );
    }


    public Optional<AccountRecord> getAccountData(AuthenticationRequest authenticationRequest) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withoutProcedureColumnMetaDataAccess()
                .withSchemaName(ObjetosOracle.SISCAS)
                .withCatalogName(ObjetosOracle.CAPK_AFILIADO)
                .withProcedureName(ObjetosOracle.CASP_RECOVER_DATA)
                .declareParameters(new SqlParameter("PI_USUARIO", VARCHAR),
                        new SqlOutParameter(PO_V_ERR_COD, VARCHAR),
                        new SqlOutParameter(PO_V_ERR_MSG, VARCHAR),
                        new SqlOutParameter("PO_CURSOR", REF_CURSOR, (RowMapper<AccountRecord>) (rs, rowNum) -> {

                            var email = rs.getString("V_DE_CORREO");
                            var firstLogin = rs.getString("V_FL_PRIMER_LOGIN");

                            AccountRecord accountRecord = new AccountRecord(
                                    email,
                                    rs.getString("V_NU_DOCUMENTO"),
                                    rs.getString("NO_V_CIUDADANO"),
                                    rs.getString("AP_V_PATERNO"),
                                    rs.getString("AP_V_MATERNO"),
                                    rs.getString("V_NU_TELEFONO"),
                                    rs.getString("V_ID_CASILLA")
                            );
                            accountRecord.setFirstLogin(firstLogin);
                            accountRecord.setTipoUsr(rs.getString("V_ID_N_TIPO_PERSONA"));
                            accountRecord.setCuentaId(rs.getString("V_ID_CASILLA"));
                            accountRecord.setAbogado(rs.getString("V_FL_C_ABOGADO"));
                            accountRecord.setIdPersona(rs.getString("V_ID_PERSONA"));
                            accountRecord.setUsuario(rs.getString("V_CO_USUARIO"));
                            accountRecord.setDefensorPublico(rs.getString("V_FL_C_DEFENSOR"));
                            accountRecord.setTiene2fa(rs.getString("V_C_FL_2FA"));
                            accountRecord.setCuentaBloqueada(rs.getString("V_C_FL_BLOQUEADA"));
                            return accountRecord;
                        }));

        SqlParameterSource objParametrosIN = new MapSqlParameterSource()
                .addValue("PI_USUARIO", authenticationRequest.usuario());

        Map<String, Object> res = jdbcCall.execute(objParametrosIN);

        if (SP_INTERNAL_ERROR.getValue().equals(res.get(PO_V_ERR_COD).toString())) {
            log.error("Error en %s: %s".formatted(authenticationRequest.usuario(), res.get(PO_V_ERR_MSG)));
            throw new InternalServerErrorException();
        }

        if (SP_ERROR_STATUS.getValue().equals(res.get(PO_V_ERR_COD).toString())) {
            log.info("%s :  %s ".formatted(authenticationRequest.usuario(), res.get(PO_V_ERR_MSG).toString()));
            throw new HttpStatusResponseException(HttpStatus.BAD_REQUEST, res.get(PO_V_ERR_MSG).toString());
        }

        var list = Optional.ofNullable((List<?>) res.get("PO_CURSOR")).orElseGet(ArrayList::new);
        if (list.isEmpty()) {
            log.info("Usuario no encontrado: %s".formatted(authenticationRequest.usuario()));
            return Optional.empty();
        }
        return Optional.of((AccountRecord) list.get(0));


    }


    public ValidateCodeResponse updateEmail(UpdateEmailRequest updateEmailRequest, String userid) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withoutProcedureColumnMetaDataAccess()
                .withSchemaName(ObjetosOracle.SISCAS)
                .withCatalogName(ObjetosOracle.CAPK_AFILIADO)
                .withProcedureName(ObjetosOracle.CASP_ACTUALIZAR_CORREO)
                .declareParameters(
                        new SqlParameter("PI_V_CORREO", VARCHAR),
                        new SqlParameter("PI_V_ID_CASILLA", VARCHAR),
                        new SqlOutParameter(PO_V_ERR_COD, VARCHAR),
                        new SqlOutParameter(PO_V_ERR_MSG, VARCHAR)
                );

        SqlParameterSource objParametrosIN = new MapSqlParameterSource()
                .addValue("PI_V_ID_CASILLA", userid)
                .addValue("PI_V_CORREO", updateEmailRequest.email());

        Map<String, Object> res = jdbcCall.execute(objParametrosIN);
        log.info(res.toString());
        if (SP_INTERNAL_ERROR.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.error("Error en %s - %s".formatted(userid, res.get(PO_V_ERR_MSG).toString()));
            log.info(res.toString());
            throw new InternalServerErrorException();
        }
        if (SP_ERROR_STATUS.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.info("%s - %s".formatted(userid, res.get(PO_V_ERR_MSG).toString()));
            throw new HttpStatusResponseException(BAD_REQUEST, res.get(PO_V_ERR_MSG).toString());
        }
        return new ValidateCodeResponse();
    }

    public ValidateCodeResponse validate2FA(UserDetail userDetail) {

        var userid = userDetail.getIdCasilla();

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withoutProcedureColumnMetaDataAccess()
            .withSchemaName(ObjetosOracle.SISCAS)
            .withCatalogName(CAPK_SEGURIDAD)
            .withProcedureName(ObjetosOracle.CASP_VALIDAR_2FA)
            .declareParameters(
                new SqlParameter(PI_V_ID_CASILLA, VARCHAR),
                new SqlOutParameter(PO_V_FL_2FA, VARCHAR),
                new SqlOutParameter(PO_V_ERR_COD, VARCHAR),
                new SqlOutParameter(PO_V_ERR_MSG, VARCHAR)
            );

        SqlParameterSource objParametrosIN = new MapSqlParameterSource()
                .addValue(PI_V_ID_CASILLA, userDetail.getIdCasilla());

        Map<String, Object> res = jdbcCall.execute(objParametrosIN);
        log.info(res.toString());

        if (SP_INTERNAL_ERROR.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.error("Error en %s - %s".formatted(userid, res.get(PO_V_ERR_MSG).toString()));
            log.info(res.toString());
            throw new InternalServerErrorException();
        }

        if (SP_ERROR_STATUS.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.info("%s - %s".formatted(userid, res.get(PO_V_ERR_MSG).toString()));
            throw new HttpStatusResponseException(BAD_REQUEST, res.get(PO_V_ERR_MSG).toString());
        }

        if ("0".equals(((String) res.get(PO_V_FL_2FA)).trim())) {
            return new ValidateCodeResponse(true, "El doble factor de autenticación NO se encuentra activo");
        }

        return new ValidateCodeResponse();
    }

    public ValidateCodeResponse activate2FA(UserDetail userDetail, Manage2FARequest manage2FARequest) {

        var userid = userDetail.getIdCasilla();

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withoutProcedureColumnMetaDataAccess()
                .withSchemaName(ObjetosOracle.SISCAS)
                .withCatalogName(CAPK_SEGURIDAD)
                .withProcedureName(ObjetosOracle.CASP_GESTION_2FA)
                .declareParameters(
                        new SqlParameter(PI_V_ID_CASILLA, VARCHAR),
                        new SqlParameter(PI_C_FL_2FA, CHAR),
                        new SqlParameter(PI_V_CO_US_CREACION, VARCHAR),
                        new SqlOutParameter(PO_V_ERR_COD, VARCHAR),
                        new SqlOutParameter(PO_V_ERR_MSG, VARCHAR)
                );

        SqlParameterSource objParametrosIN = new MapSqlParameterSource()
                .addValue(PI_V_ID_CASILLA, userDetail.getIdCasilla())
                .addValue(PI_C_FL_2FA, manage2FARequest.getActivation2FA())
                .addValue(PI_V_CO_US_CREACION, userDetail.getDni());

        Map<String, Object> res = jdbcCall.execute(objParametrosIN);
        log.info(res.toString());

        if (SP_INTERNAL_ERROR.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.error("Error en %s - %s".formatted(userid, res.get(PO_V_ERR_MSG).toString()));
            log.info(res.toString());
            throw new InternalServerErrorException();
        }
        if (SP_ERROR_STATUS.getValue().equals(res.get(PO_V_ERR_COD))) {
            log.info("%s - %s".formatted(userid, res.get(PO_V_ERR_MSG).toString()));
            throw new HttpStatusResponseException(BAD_REQUEST, res.get(PO_V_ERR_MSG).toString());
        }

        return new ValidateCodeResponse();
    }

}
