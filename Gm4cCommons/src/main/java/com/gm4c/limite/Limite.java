/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.gm4c.limite;

import org.apache.avro.specific.SpecificData;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class Limite extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 8038290247879879604L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Limite\",\"namespace\":\"com.gm4c.limite\",\"fields\":[{\"name\":\"id_simulacao\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"id da simulacao\"},{\"name\":\"agencia\",\"type\":\"int\",\"doc\":\"Codigo da agencia de origem\"},{\"name\":\"conta\",\"type\":\"int\",\"doc\":\"Codigo da conta de origem\"},{\"name\":\"dv\",\"type\":\"int\",\"doc\":\"Dac da conta de origem\"},{\"name\":\"valor\",\"type\":\"float\",\"doc\":\"Valor da Transferencia\"},{\"name\":\"aprovado\",\"type\":\"boolean\",\"doc\":\"Retorna verdadeiro se houver limite ainda no dia\"},{\"name\":\"evento\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"Retorna o tipo de evento\"}],\"version\":\"1\"}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<Limite> ENCODER =
      new BinaryMessageEncoder<Limite>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<Limite> DECODER =
      new BinaryMessageDecoder<Limite>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   */
  public static BinaryMessageDecoder<Limite> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   */
  public static BinaryMessageDecoder<Limite> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<Limite>(MODEL$, SCHEMA$, resolver);
  }

  /** Serializes this Limite to a ByteBuffer. */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /** Deserializes a Limite from a ByteBuffer. */
  public static Limite fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  /** id da simulacao */
   private java.lang.String id_simulacao;
  /** Codigo da agencia de origem */
   private int agencia;
  /** Codigo da conta de origem */
   private int conta;
  /** Dac da conta de origem */
   private int dv;
  /** Valor da Transferencia */
   private float valor;
  /** Retorna verdadeiro se houver limite ainda no dia */
   private boolean aprovado;
  /** Retorna o tipo de evento */
   private java.lang.String evento;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public Limite() {}

  /**
   * All-args constructor.
   * @param id_simulacao id da simulacao
   * @param agencia Codigo da agencia de origem
   * @param conta Codigo da conta de origem
   * @param dv Dac da conta de origem
   * @param valor Valor da Transferencia
   * @param aprovado Retorna verdadeiro se houver limite ainda no dia
   * @param evento Retorna o tipo de evento
   */
  public Limite(java.lang.String id_simulacao, java.lang.Integer agencia, java.lang.Integer conta, java.lang.Integer dv, java.lang.Float valor, java.lang.Boolean aprovado, java.lang.String evento) {
    this.id_simulacao = id_simulacao;
    this.agencia = agencia;
    this.conta = conta;
    this.dv = dv;
    this.valor = valor;
    this.aprovado = aprovado;
    this.evento = evento;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return id_simulacao;
    case 1: return agencia;
    case 2: return conta;
    case 3: return dv;
    case 4: return valor;
    case 5: return aprovado;
    case 6: return evento;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: id_simulacao = (java.lang.String)value$; break;
    case 1: agencia = (java.lang.Integer)value$; break;
    case 2: conta = (java.lang.Integer)value$; break;
    case 3: dv = (java.lang.Integer)value$; break;
    case 4: valor = (java.lang.Float)value$; break;
    case 5: aprovado = (java.lang.Boolean)value$; break;
    case 6: evento = (java.lang.String)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'id_simulacao' field.
   * @return id da simulacao
   */
  public java.lang.String getIdSimulacao() {
    return id_simulacao;
  }

  /**
   * Sets the value of the 'id_simulacao' field.
   * id da simulacao
   * @param value the value to set.
   */
  public void setIdSimulacao(java.lang.String value) {
    this.id_simulacao = value;
  }

  /**
   * Gets the value of the 'agencia' field.
   * @return Codigo da agencia de origem
   */
  public java.lang.Integer getAgencia() {
    return agencia;
  }

  /**
   * Sets the value of the 'agencia' field.
   * Codigo da agencia de origem
   * @param value the value to set.
   */
  public void setAgencia(java.lang.Integer value) {
    this.agencia = value;
  }

  /**
   * Gets the value of the 'conta' field.
   * @return Codigo da conta de origem
   */
  public java.lang.Integer getConta() {
    return conta;
  }

  /**
   * Sets the value of the 'conta' field.
   * Codigo da conta de origem
   * @param value the value to set.
   */
  public void setConta(java.lang.Integer value) {
    this.conta = value;
  }

  /**
   * Gets the value of the 'dv' field.
   * @return Dac da conta de origem
   */
  public java.lang.Integer getDv() {
    return dv;
  }

  /**
   * Sets the value of the 'dv' field.
   * Dac da conta de origem
   * @param value the value to set.
   */
  public void setDv(java.lang.Integer value) {
    this.dv = value;
  }

  /**
   * Gets the value of the 'valor' field.
   * @return Valor da Transferencia
   */
  public java.lang.Float getValor() {
    return valor;
  }

  /**
   * Sets the value of the 'valor' field.
   * Valor da Transferencia
   * @param value the value to set.
   */
  public void setValor(java.lang.Float value) {
    this.valor = value;
  }

  /**
   * Gets the value of the 'aprovado' field.
   * @return Retorna verdadeiro se houver limite ainda no dia
   */
  public java.lang.Boolean getAprovado() {
    return aprovado;
  }

  /**
   * Sets the value of the 'aprovado' field.
   * Retorna verdadeiro se houver limite ainda no dia
   * @param value the value to set.
   */
  public void setAprovado(java.lang.Boolean value) {
    this.aprovado = value;
  }

  /**
   * Gets the value of the 'evento' field.
   * @return Retorna o tipo de evento
   */
  public java.lang.String getEvento() {
    return evento;
  }

  /**
   * Sets the value of the 'evento' field.
   * Retorna o tipo de evento
   * @param value the value to set.
   */
  public void setEvento(java.lang.String value) {
    this.evento = value;
  }

  /**
   * Creates a new Limite RecordBuilder.
   * @return A new Limite RecordBuilder
   */
  public static com.gm4c.limite.Limite.Builder newBuilder() {
    return new com.gm4c.limite.Limite.Builder();
  }

  /**
   * Creates a new Limite RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new Limite RecordBuilder
   */
  public static com.gm4c.limite.Limite.Builder newBuilder(com.gm4c.limite.Limite.Builder other) {
    return new com.gm4c.limite.Limite.Builder(other);
  }

  /**
   * Creates a new Limite RecordBuilder by copying an existing Limite instance.
   * @param other The existing instance to copy.
   * @return A new Limite RecordBuilder
   */
  public static com.gm4c.limite.Limite.Builder newBuilder(com.gm4c.limite.Limite other) {
    return new com.gm4c.limite.Limite.Builder(other);
  }

  /**
   * RecordBuilder for Limite instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Limite>
    implements org.apache.avro.data.RecordBuilder<Limite> {

    /** id da simulacao */
    private java.lang.String id_simulacao;
    /** Codigo da agencia de origem */
    private int agencia;
    /** Codigo da conta de origem */
    private int conta;
    /** Dac da conta de origem */
    private int dv;
    /** Valor da Transferencia */
    private float valor;
    /** Retorna verdadeiro se houver limite ainda no dia */
    private boolean aprovado;
    /** Retorna o tipo de evento */
    private java.lang.String evento;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.gm4c.limite.Limite.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.id_simulacao)) {
        this.id_simulacao = data().deepCopy(fields()[0].schema(), other.id_simulacao);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.agencia)) {
        this.agencia = data().deepCopy(fields()[1].schema(), other.agencia);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.conta)) {
        this.conta = data().deepCopy(fields()[2].schema(), other.conta);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.dv)) {
        this.dv = data().deepCopy(fields()[3].schema(), other.dv);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.valor)) {
        this.valor = data().deepCopy(fields()[4].schema(), other.valor);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.aprovado)) {
        this.aprovado = data().deepCopy(fields()[5].schema(), other.aprovado);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.evento)) {
        this.evento = data().deepCopy(fields()[6].schema(), other.evento);
        fieldSetFlags()[6] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing Limite instance
     * @param other The existing instance to copy.
     */
    private Builder(com.gm4c.limite.Limite other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.id_simulacao)) {
        this.id_simulacao = data().deepCopy(fields()[0].schema(), other.id_simulacao);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.agencia)) {
        this.agencia = data().deepCopy(fields()[1].schema(), other.agencia);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.conta)) {
        this.conta = data().deepCopy(fields()[2].schema(), other.conta);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.dv)) {
        this.dv = data().deepCopy(fields()[3].schema(), other.dv);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.valor)) {
        this.valor = data().deepCopy(fields()[4].schema(), other.valor);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.aprovado)) {
        this.aprovado = data().deepCopy(fields()[5].schema(), other.aprovado);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.evento)) {
        this.evento = data().deepCopy(fields()[6].schema(), other.evento);
        fieldSetFlags()[6] = true;
      }
    }

    /**
      * Gets the value of the 'id_simulacao' field.
      * id da simulacao
      * @return The value.
      */
    public java.lang.String getIdSimulacao() {
      return id_simulacao;
    }

    /**
      * Sets the value of the 'id_simulacao' field.
      * id da simulacao
      * @param value The value of 'id_simulacao'.
      * @return This builder.
      */
    public com.gm4c.limite.Limite.Builder setIdSimulacao(java.lang.String value) {
      validate(fields()[0], value);
      this.id_simulacao = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'id_simulacao' field has been set.
      * id da simulacao
      * @return True if the 'id_simulacao' field has been set, false otherwise.
      */
    public boolean hasIdSimulacao() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'id_simulacao' field.
      * id da simulacao
      * @return This builder.
      */
    public com.gm4c.limite.Limite.Builder clearIdSimulacao() {
      id_simulacao = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'agencia' field.
      * Codigo da agencia de origem
      * @return The value.
      */
    public java.lang.Integer getAgencia() {
      return agencia;
    }

    /**
      * Sets the value of the 'agencia' field.
      * Codigo da agencia de origem
      * @param value The value of 'agencia'.
      * @return This builder.
      */
    public com.gm4c.limite.Limite.Builder setAgencia(int value) {
      validate(fields()[1], value);
      this.agencia = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'agencia' field has been set.
      * Codigo da agencia de origem
      * @return True if the 'agencia' field has been set, false otherwise.
      */
    public boolean hasAgencia() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'agencia' field.
      * Codigo da agencia de origem
      * @return This builder.
      */
    public com.gm4c.limite.Limite.Builder clearAgencia() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'conta' field.
      * Codigo da conta de origem
      * @return The value.
      */
    public java.lang.Integer getConta() {
      return conta;
    }

    /**
      * Sets the value of the 'conta' field.
      * Codigo da conta de origem
      * @param value The value of 'conta'.
      * @return This builder.
      */
    public com.gm4c.limite.Limite.Builder setConta(int value) {
      validate(fields()[2], value);
      this.conta = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'conta' field has been set.
      * Codigo da conta de origem
      * @return True if the 'conta' field has been set, false otherwise.
      */
    public boolean hasConta() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'conta' field.
      * Codigo da conta de origem
      * @return This builder.
      */
    public com.gm4c.limite.Limite.Builder clearConta() {
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'dv' field.
      * Dac da conta de origem
      * @return The value.
      */
    public java.lang.Integer getDv() {
      return dv;
    }

    /**
      * Sets the value of the 'dv' field.
      * Dac da conta de origem
      * @param value The value of 'dv'.
      * @return This builder.
      */
    public com.gm4c.limite.Limite.Builder setDv(int value) {
      validate(fields()[3], value);
      this.dv = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'dv' field has been set.
      * Dac da conta de origem
      * @return True if the 'dv' field has been set, false otherwise.
      */
    public boolean hasDv() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'dv' field.
      * Dac da conta de origem
      * @return This builder.
      */
    public com.gm4c.limite.Limite.Builder clearDv() {
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'valor' field.
      * Valor da Transferencia
      * @return The value.
      */
    public java.lang.Float getValor() {
      return valor;
    }

    /**
      * Sets the value of the 'valor' field.
      * Valor da Transferencia
      * @param value The value of 'valor'.
      * @return This builder.
      */
    public com.gm4c.limite.Limite.Builder setValor(float value) {
      validate(fields()[4], value);
      this.valor = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'valor' field has been set.
      * Valor da Transferencia
      * @return True if the 'valor' field has been set, false otherwise.
      */
    public boolean hasValor() {
      return fieldSetFlags()[4];
    }


    /**
      * Clears the value of the 'valor' field.
      * Valor da Transferencia
      * @return This builder.
      */
    public com.gm4c.limite.Limite.Builder clearValor() {
      fieldSetFlags()[4] = false;
      return this;
    }

    /**
      * Gets the value of the 'aprovado' field.
      * Retorna verdadeiro se houver limite ainda no dia
      * @return The value.
      */
    public java.lang.Boolean getAprovado() {
      return aprovado;
    }

    /**
      * Sets the value of the 'aprovado' field.
      * Retorna verdadeiro se houver limite ainda no dia
      * @param value The value of 'aprovado'.
      * @return This builder.
      */
    public com.gm4c.limite.Limite.Builder setAprovado(boolean value) {
      validate(fields()[5], value);
      this.aprovado = value;
      fieldSetFlags()[5] = true;
      return this;
    }

    /**
      * Checks whether the 'aprovado' field has been set.
      * Retorna verdadeiro se houver limite ainda no dia
      * @return True if the 'aprovado' field has been set, false otherwise.
      */
    public boolean hasAprovado() {
      return fieldSetFlags()[5];
    }


    /**
      * Clears the value of the 'aprovado' field.
      * Retorna verdadeiro se houver limite ainda no dia
      * @return This builder.
      */
    public com.gm4c.limite.Limite.Builder clearAprovado() {
      fieldSetFlags()[5] = false;
      return this;
    }

    /**
      * Gets the value of the 'evento' field.
      * Retorna o tipo de evento
      * @return The value.
      */
    public java.lang.String getEvento() {
      return evento;
    }

    /**
      * Sets the value of the 'evento' field.
      * Retorna o tipo de evento
      * @param value The value of 'evento'.
      * @return This builder.
      */
    public com.gm4c.limite.Limite.Builder setEvento(java.lang.String value) {
      validate(fields()[6], value);
      this.evento = value;
      fieldSetFlags()[6] = true;
      return this;
    }

    /**
      * Checks whether the 'evento' field has been set.
      * Retorna o tipo de evento
      * @return True if the 'evento' field has been set, false otherwise.
      */
    public boolean hasEvento() {
      return fieldSetFlags()[6];
    }


    /**
      * Clears the value of the 'evento' field.
      * Retorna o tipo de evento
      * @return This builder.
      */
    public com.gm4c.limite.Limite.Builder clearEvento() {
      evento = null;
      fieldSetFlags()[6] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Limite build() {
      try {
        Limite record = new Limite();
        record.id_simulacao = fieldSetFlags()[0] ? this.id_simulacao : (java.lang.String) defaultValue(fields()[0]);
        record.agencia = fieldSetFlags()[1] ? this.agencia : (java.lang.Integer) defaultValue(fields()[1]);
        record.conta = fieldSetFlags()[2] ? this.conta : (java.lang.Integer) defaultValue(fields()[2]);
        record.dv = fieldSetFlags()[3] ? this.dv : (java.lang.Integer) defaultValue(fields()[3]);
        record.valor = fieldSetFlags()[4] ? this.valor : (java.lang.Float) defaultValue(fields()[4]);
        record.aprovado = fieldSetFlags()[5] ? this.aprovado : (java.lang.Boolean) defaultValue(fields()[5]);
        record.evento = fieldSetFlags()[6] ? this.evento : (java.lang.String) defaultValue(fields()[6]);
        return record;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<Limite>
    WRITER$ = (org.apache.avro.io.DatumWriter<Limite>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<Limite>
    READER$ = (org.apache.avro.io.DatumReader<Limite>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}
