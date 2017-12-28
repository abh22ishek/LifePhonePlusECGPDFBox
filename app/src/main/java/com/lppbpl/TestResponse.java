/*
 *
 */
package com.lppbpl;
// Generated by proto2javame, Thu Jan 17 09:36:51 IST 2013.

import java.io.IOException;
import java.io.InputStream;
import net.jarlehansen.protobuf.javame.UninitializedMessageException;
import net.jarlehansen.protobuf.javame.input.InputReader;
import net.jarlehansen.protobuf.javame.input.DelimitedInputStream;
import net.jarlehansen.protobuf.javame.input.DelimitedSizeUtil;
import net.jarlehansen.protobuf.javame.ComputeSizeUtil;
import net.jarlehansen.protobuf.javame.output.OutputWriter;
import net.jarlehansen.protobuf.javame.AbstractOutputWriter;
import net.jarlehansen.protobuf.javame.input.taghandler.UnknownTagHandler;
import net.jarlehansen.protobuf.javame.input.taghandler.DefaultUnknownTagHandlerImpl;

// TODO: Auto-generated Javadoc
/**
 * The Class TestResponse.
 */
public final class TestResponse extends AbstractOutputWriter {

	/** The unknown tag handler. */
	private static UnknownTagHandler unknownTagHandler = DefaultUnknownTagHandlerImpl.newInstance();

	/** The result. */
	private final int result;

	/** The Constant fieldNumberResult. */
	private static final int fieldNumberResult = 1;

	/** The message. */
	private final String message;

	/** The Constant fieldNumberMessage. */
	private static final int fieldNumberMessage = 2;

	/** The has message. */
	private final boolean hasMessage;


	/**
	 * Method newBuilder.
	 * @return Builder
	 */
	public static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * Constructor for TestResponse.
	 * @param builder Builder
	 */
	private TestResponse(final Builder builder) {
		if (builder.hasResult ) {
			this.result = builder.result;
			this.message = builder.message;
			this.hasMessage = builder.hasMessage;
		} else {
			throw new UninitializedMessageException("Not all required fields were included (false = not included in message), " +
				" result:" + builder.hasResult + "");
		}
	}

	/**
	 * The Class Result.
	 */
	public static class Result {

		/** The Constant Pass. */
		public static final int Pass = 1;

		/** The Constant Fail. */
		public static final int Fail = 2;

		/**
		 * Method getStringValue.
		 * @param value int
		 * @return String
		 */
		public static String getStringValue(int value) {
			String retValue;

			switch(value) {
				case 1:
					retValue = "Pass";
					break;
				case 2:
					retValue = "Fail";
					break;
				default:
					retValue = "";
					break;
			}

			return retValue;
		}
	}

	/**
	 * The Class Builder.
	 */
	public static class Builder {

		/** The result. */
		private int result;

		/** The has result. */
		private boolean hasResult = false;

		/** The message. */
		private String message;

		/** The has message. */
		private boolean hasMessage = false;


		/**
		 * Instantiates a new builder.
		 */
		private Builder() {
		}

		/**
		 * Method setResult.
		 * @param result int
		 * @return Builder
		 */
		public Builder setResult(final int result) {
			this.result = result;
			this.hasResult = true;
			return this;
		}

		/**
		 * Method setMessage.
		 * @param message String
		 * @return Builder
		 */
		public Builder setMessage(final String message) {
			this.message = message;
			this.hasMessage = true;
			return this;
		}

		/**
		 * Method build.
		 * @return TestResponse
		 */
		public TestResponse build() {
			return new TestResponse(this);
		}
	}

	/**
	 * Method getResult.
	 * @return int
	 */
	public int getResult() {
		return result;
	}

	/**
	 * Method getMessage.
	 * @return String
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Method hasMessage.
	 * @return boolean
	 */
	public boolean hasMessage() {
		return hasMessage;
	}

	/**
	 * Method toString.
	 * @return String
	 */
	public String toString() {
		final String TAB = "   ";
		String retValue = "";
		retValue += this.getClass().getName() + "(";
		retValue += "result = " + this.result + TAB;
		if(hasMessage) retValue += "message = " + this.message + TAB;
		retValue += ")";

		return retValue;
	}

	// Override
	/**
	 * Method computeSize.
	 * @return int
	 * @see net.jarlehansen.protobuf.javame.CustomListWriter#computeSize()
	 */
	public int computeSize() {
		int totalSize = 0;
		totalSize += ComputeSizeUtil.computeIntSize(fieldNumberResult, result);
		if(hasMessage) totalSize += ComputeSizeUtil.computeStringSize(fieldNumberMessage, message);
		totalSize += computeNestedMessageSize();

		return totalSize;
	}

	/**
	 * Method computeNestedMessageSize.
	 * @return int
	 */
	private int computeNestedMessageSize() {
		int messageSize = 0;

		return messageSize;
	}

	// Override
	/**
	 * Method writeFields.
	 *
	 * @param writer OutputWriter
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @see net.jarlehansen.protobuf.javame.CustomListWriter#writeFields(OutputWriter)
	 */
	public void writeFields(final OutputWriter writer) throws IOException {
		writer.writeInt(fieldNumberResult, result);
		if(hasMessage) writer.writeString(fieldNumberMessage, message);
	}

	/**
	 * Method parseFields.
	 *
	 * @param reader InputReader
	 * @return TestResponse
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	static TestResponse parseFields(final InputReader reader) throws IOException {
		int nextFieldNumber = getNextFieldNumber(reader);
		final Builder builder = TestResponse.newBuilder();

		while (nextFieldNumber > 0) {
			if(!populateBuilderWithField(reader, builder, nextFieldNumber)) {
				reader.getPreviousTagDataTypeAndReadContent();
			}
			nextFieldNumber = getNextFieldNumber(reader);
		}

		return builder.build();
	}

	/**
	 * Method getNextFieldNumber.
	 *
	 * @param reader InputReader
	 * @return int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	static int getNextFieldNumber(final InputReader reader) throws IOException {
		return reader.getNextFieldNumber();
	}

	/**
	 * Method populateBuilderWithField.
	 *
	 * @param reader InputReader
	 * @param builder Builder
	 * @param fieldNumber int
	 * @return boolean
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	static boolean populateBuilderWithField(final InputReader reader, final Builder builder, final int fieldNumber) throws IOException {
		boolean fieldFound = true;
		switch (fieldNumber) {
			case fieldNumberResult:
				builder.setResult(reader.readInt(fieldNumber));
				break;
			case fieldNumberMessage:
				builder.setMessage(reader.readString(fieldNumber));
				break;
		default:
			fieldFound = false;
		}
		return fieldFound;
	}

	/**
	 * Method setUnknownTagHandler.
	 * @param unknownTagHandler UnknownTagHandler
	 */
	public static void setUnknownTagHandler(final UnknownTagHandler unknownTagHandler) {
		TestResponse.unknownTagHandler = unknownTagHandler;
	}

	/**
	 * Method parseFrom.
	 *
	 * @param data byte[]
	 * @return TestResponse
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static TestResponse parseFrom(final byte[] data) throws IOException {
		return parseFields(new InputReader(data, unknownTagHandler));
	}

	/**
	 * Method parseFrom.
	 *
	 * @param inputStream InputStream
	 * @return TestResponse
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static TestResponse parseFrom(final InputStream inputStream) throws IOException {
		return parseFields(new InputReader(inputStream, unknownTagHandler));
	}

	/**
	 * Method parseDelimitedFrom.
	 *
	 * @param inputStream InputStream
	 * @return TestResponse
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static TestResponse parseDelimitedFrom(final InputStream inputStream) throws IOException {
		final int limit = DelimitedSizeUtil.readDelimitedSize(inputStream);
		return parseFields(new InputReader(new DelimitedInputStream(inputStream, limit), unknownTagHandler));
	}
}