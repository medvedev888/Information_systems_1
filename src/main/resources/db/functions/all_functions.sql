DROP FUNCTION IF EXISTS count_organizations_by_rating(DOUBLE PRECISION);
DROP FUNCTION IF EXISTS get_organizations_by_type(VARCHAR);
DROP FUNCTION IF EXISTS get_unique_fullnames();
DROP FUNCTION IF EXISTS merge_organizations(BIGINT, BIGINT, VARCHAR, BIGINT);
DROP FUNCTION IF EXISTS absorb_organization(BIGINT, BIGINT);


-- 1) Количество организаций с rating < заданного
CREATE OR REPLACE FUNCTION count_organizations_by_rating(min_rating DOUBLE PRECISION)
    RETURNS BIGINT AS
$$
BEGIN
    RETURN (SELECT COUNT(*) FROM organizations WHERE rating < min_rating);
END;
$$ LANGUAGE plpgsql;


-- 2) Получить организации по type
CREATE OR REPLACE FUNCTION get_organizations_by_type(p_type VARCHAR)
    RETURNS TABLE (
                      id                  INT,
                      annual_turnover     DOUBLE PRECISION,
                      creationdate        TIMESTAMP(6),
                      employees_count     INTEGER,
                      fullname            VARCHAR(255),
                      name                VARCHAR(255),
                      rating              DOUBLE PRECISION,
                      type                VARCHAR(255),
                      coordinates_id      BIGINT,
                      official_address_id BIGINT,
                      postal_address_id   BIGINT
                  )
AS $$
BEGIN
    RETURN QUERY
        SELECT o.id,
               o.annual_turnover,
               o.creationdate,
               o.employees_count,
               o.fullname,
               o.name,
               o.rating,
               o.type,
               o.coordinates_id,
               o.official_address_id,
               o.postal_address_id
        FROM organizations o
        WHERE o.type = p_type;
END;
$$ LANGUAGE plpgsql;



-- 3) Получить уникальные fullName
CREATE OR REPLACE FUNCTION get_unique_fullnames()
    RETURNS TABLE
            (
                fullname VARCHAR(255)
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT DISTINCT o.fullname AS fullname FROM organizations o;
END;
$$ LANGUAGE plpgsql;


-- 4) Объединить организации
CREATE OR REPLACE FUNCTION merge_organizations(
    org1_id INT,
    org2_id INT,
    new_name VARCHAR,
    new_official_address_id BIGINT
)
    RETURNS TABLE (
                      id                  INT,
                      fullname            VARCHAR(255),
                      name                VARCHAR(255),
                      employees_count     INT,
                      annual_turnover     DOUBLE PRECISION,
                      creationdate        TIMESTAMP(6),
                      rating              DOUBLE PRECISION,
                      type                VARCHAR(255),
                      coordinates_id      BIGINT,
                      official_address_id BIGINT,
                      postal_address_id   BIGINT
                  )
AS
$$
BEGIN
    UPDATE organizations o1
    SET
        name                = new_name,
        official_address_id = new_official_address_id,
        employees_count     = o1.employees_count + (SELECT o2.employees_count FROM organizations o2 WHERE o2.id = org2_id),
        annual_turnover     = o1.annual_turnover + (SELECT o2.annual_turnover FROM organizations o2 WHERE o2.id = org2_id)
    WHERE o1.id = org1_id;

    DELETE FROM organizations o_del WHERE o_del.id = org2_id;

    RETURN QUERY
        SELECT
            o.id,
            o.fullname,
            o.name,
            o.employees_count,
            o.annual_turnover,
            o.creationdate,
            o.rating,
            o.type,
            o.coordinates_id,
            o.official_address_id,
            o.postal_address_id
        FROM organizations o
        WHERE o.id = org1_id;
END;
$$ LANGUAGE plpgsql;


-- 5) Поглощение одной организацией другой
CREATE OR REPLACE FUNCTION absorb_organization(
    acquirer_id INT,
    victim_id INT
)
    RETURNS VOID AS
$$
BEGIN
    UPDATE organizations o_acq
    SET
        employees_count = o_acq.employees_count + (SELECT o_v.employees_count FROM organizations o_v WHERE o_v.id = victim_id),
        annual_turnover = o_acq.annual_turnover + (SELECT o_v.annual_turnover FROM organizations o_v WHERE o_v.id = victim_id)
    WHERE o_acq.id = acquirer_id;

    DELETE FROM organizations o_del WHERE o_del.id = victim_id;
END;
$$ LANGUAGE plpgsql;