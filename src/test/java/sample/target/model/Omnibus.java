package sample.target.model;

import com.hedera.hashgraph.protoparse.OneOf;

import java.util.List;

public record Omnibus(
        int int32Number, long int64Number, int uint32Number, long uint64Number,
        boolean flag, Suit suitEnum,
        int sint32Number, long sint64Number,
        int sfixed32Number, long sfixed64Number, int fixed32Number, long fixed64Number,
        float floatNumber, double doubleNumber,
        String memo, byte[] randomBytes, Nested nested,
        OneOf<Fruits.FruitKind, Object> fruit,
        OneOf<Everything, Object> everything,
        List<Integer> int32NumberList, List<Long> int64NumberList,
        List<Integer> uint32NumberList, List<Long> uint64NumberList,
        List<Boolean> flagList, List<Suit> suitEnumList,
        List<Integer> sint32NumberList, List<Long> sint64NumberList,
        List<Integer> sfixed32NumberList, List<Long> sfixed64NumberList,
        List<Integer> fixed32NumberList, List<Long> fixed64NumberList,
        List<Float> floatNumberList, List<Double> doubleNumberList,
        List<String> memoList, List<byte[]> randomBytesList,
        List<Nested> nestedList, List<Object> fruitList) {
    public enum Everything {
        INT32,
        INT64,
        UINT32,
        UINT64,
        FLAG,
        SUIT,
        SINT32,
        SINT64,
        SFIXED32,
        SFIXED64,
        FIXED32,
        FIXED64,
        FLOAT,
        DOUBLE,
        MEMO,
        RANDOM_BYTES,
        NESTED
    }
}
