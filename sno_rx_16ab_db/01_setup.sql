CREATE SCHEMA umls;

use umls;

CREATE TABLE cui_terms (
  cui BIGINT NOT NULL,
  rindex INT(128) NOT NULL,
  tcount INT(128) NOT NULL,
  text VARCHAR(255) NOT NULL,
  rword VARCHAR(48) NOT NULL
);
CREATE INDEX idx_cui_terms ON cui_terms (rword);

CREATE TABLE tui (
  cui BIGINT NOT NULL,
  tui INT(128) NOT NULL
);
CREATE INDEX idx_tui ON tui (cui);

CREATE TABLE prefterm (
  cui BIGINT NOT NULL,
  prefterm VARCHAR(511) NOT NULL
);
CREATE INDEX idx_prefterm ON prefterm (cui);

CREATE TABLE rxnorm (
  cui BIGINT NOT NULL,
  rxnorm BIGINT NOT NULL
);
CREATE INDEX idx_rxnorm ON rxnorm (cui);

CREATE TABLE snomedct_us (
  cui BIGINT NOT NULL,
  snomedct_us BIGINT NOT NULL
);
CREATE INDEX idx_snomedct_us ON snomedct_us (cui);
