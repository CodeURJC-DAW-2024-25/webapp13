package es.codeurjc13.librored.dto.external;

import java.util.List;

public class OpenLibraryAuthorSearchResponseDTO {
    private List<AuthorData> docs;

    public List<AuthorData> getDocs() {
        return docs;
    }

    public void setDocs(List<AuthorData> docs) {
        this.docs = docs;
    }

    public static class AuthorData {
        private String name;
        private String birth_date;
        private String top_work;
        private int work_count;

        public String getName() { return name; }

        public void setName(String name) { this.name = name; }

        public String getBirth_date() { return birth_date; }

        public void setBirth_date(String birth_date) { this.birth_date = birth_date; }

        public String getTop_work() { return top_work; }

        public void setTop_work(String top_work) { this.top_work = top_work; }

        public int getWork_count() { return work_count; }

        public void setWork_count(int work_count) { this.work_count = work_count; }

        public String getTopWork() {
            return top_work;
        }

        public void setTopWork(String top_work) {
            this.top_work = top_work;
        }

    }
}
