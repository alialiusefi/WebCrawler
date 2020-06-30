package com.softeq.webcrawler.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Keyword {

  @Id
  private Long id;

  private String keyword;
}
