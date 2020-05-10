package bean.messageBean;

import bean.RecordEntity;

import java.util.List;

public class RecordMessage extends Message{
    private List<RecordEntity> records;

    public List<RecordEntity> getRecords() {
        return records;
    }

    public void setRecords(List<RecordEntity> records) {
        this.records = records;
    }
}
