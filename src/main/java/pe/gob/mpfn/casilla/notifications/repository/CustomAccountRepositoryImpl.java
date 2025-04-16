package pe.gob.mpfn.casilla.notifications.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import pe.gob.mpfn.casilla.notifications.exception.InternalServerErrorException;
import pe.gob.mpfn.casilla.notifications.model.dto.AccountRecord;
import pe.gob.mpfn.casilla.notifications.model.dto.AffiliateResult;
import pe.gob.mpfn.casilla.notifications.model.dto.UpdateProfileRequest;
import pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.sql.Types.*;
import static pe.gob.mpfn.casilla.notifications.util.enums.AppParams.ES_ABOGADO;
import static pe.gob.mpfn.casilla.notifications.util.enums.AppParams.SP_SUCCESSFUL_STATUS;
import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.PO_V_ERR_COD;
import static pe.gob.mpfn.casilla.notifications.util.enums.ObjetosOracle.PO_V_ERR_MSG;


public class CustomAccountRepositoryImpl implements CustomAccountRepository {

    private static final Logger log = LoggerFactory.getLogger(CustomAccountRepositoryImpl.class);
    private final JdbcTemplate jdbcTemplate;

    public CustomAccountRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void updateProfile(AccountRecord account, UpdateProfileRequest request) {
        var simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName(ObjetosOracle.SISCAS)
                .withCatalogName(ObjetosOracle.CAPK_AFILIADO)
                .withProcedureName(ObjetosOracle.CASP_ACTUALIZAR_DOBLE_PERFIL)
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("PI_V_ID_CASILLA", VARCHAR),
                        new SqlParameter("PI_V_ID_PERSONA", VARCHAR),
                        new SqlParameter("PI_V_DOBLE_PERFIL", VARCHAR),
                        new SqlParameter("PI_N_TIPO_CASILLA", NUMERIC),
                        new SqlParameter("PI_V_ID_COLEGIO", VARCHAR),
                        new SqlParameter("PI_N_NU_COLEGIO", NUMERIC),
                        new SqlOutParameter(PO_V_ERR_COD, VARCHAR),
                        new SqlOutParameter(PO_V_ERR_MSG, VARCHAR)
                );

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("PI_V_ID_CASILLA", account.getCuentaId())
                .addValue("PI_V_ID_PERSONA", account.getIdPersona())
                .addValue("PI_V_DOBLE_PERFIL", ES_ABOGADO.getValue())
                .addValue("PI_N_TIPO_CASILLA", account.getTipoUsr())
                .addValue("PI_V_ID_COLEGIO", request.idColegioAbogados())
                .addValue("PI_N_NU_COLEGIO", request.numeroColegiatura());

        Map<String, Object> rs = simpleJdbcCall.execute(in);

        if (!SP_SUCCESSFUL_STATUS.getValue().equals(String.valueOf(rs.get(PO_V_ERR_COD)))) {
            log.info("error: {}", rs);
            throw new InternalServerErrorException();
        }
    }

    @Override
    public List<AffiliateResult> usersWithInitialPassword() {

        var simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName(ObjetosOracle.CASP_USUARIOS_CON_PW_INICIAL)
                .withSchemaName(ObjetosOracle.SISCAS)
                .withCatalogName(ObjetosOracle.CAPK_AFILIADO)
                .declareParameters(
                        new SqlOutParameter("PO_CURSOR", REF_CURSOR, (RowMapper<AffiliateResult>) (rs, rowNum) -> {
                            String email = rs.getString("DE_V_CORREO");
                            String coUsuario = rs.getString("CO_V_USUARIO");
                            String nombre = rs.getString("NO_V_CIUDADANO");
                            String apellidoPaterno = rs.getString("AP_V_PATERNO");
                            String apellidoMaterno = rs.getString("AP_V_MATERNO");
                            Timestamp fechaRegistro = rs.getTimestamp("FE_D_CREACION");
                            return new AffiliateResult(
                                    email,
                                    coUsuario,
                                    nombre,
                                    apellidoPaterno,
                                    apellidoMaterno,
                                    fechaRegistro
                            );
                        }),
                        new SqlOutParameter(PO_V_ERR_COD, Types.VARCHAR),
                        new SqlOutParameter(PO_V_ERR_MSG, Types.VARCHAR)
                );

        Map<String, Object> out = simpleJdbcCall.execute();

        if (!SP_SUCCESSFUL_STATUS.getValue().equals(out.get(PO_V_ERR_COD).toString())) {
            log.error("Error al recuperar los usuarios: {} ", out.get(PO_V_ERR_MSG));
        }

        return Optional.ofNullable((List<AffiliateResult>) out.
                get("PO_CURSOR")).orElseGet(ArrayList::new);
    }

    @Override
    public String encodedPw(CharSequence rawPassword) {
        var simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName(ObjetosOracle.CASP_ENCRIPTAR_PASSWORD)
                .withSchemaName(ObjetosOracle.SISCAS)
                .withCatalogName(ObjetosOracle.CAPK_AFILIADO)
                .declareParameters(
                        new SqlParameter("PI_RAW_PASSWORD", VARCHAR),
                        new SqlOutParameter("PO_ENCRYPTED_PASSWORD", VARCHAR)
                );

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("PI_RAW_PASSWORD", rawPassword);

        Map<String, Object> out = simpleJdbcCall.execute(in);

        return Optional.ofNullable((String) out.
                get("PO_ENCRYPTED_PASSWORD")).orElseGet(() -> "");
    }


}
