package com.example.soonsul.search;

import com.example.soonsul.liquor.entity.Liquor;
import com.example.soonsul.liquor.repository.LiquorRepository;
import com.example.soonsul.search.dto.SearchDto;
import com.example.soonsul.util.LiquorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final LiquorRepository liquorRepository;
    private final LiquorUtil liquorUtil;


    @Transactional(readOnly = true)
    public List<SearchDto> getSearch(String name){
        final List<Liquor> list= liquorRepository.findSearch(name);
        final List<SearchDto> result= new ArrayList<>();

        for(Liquor l: list){
            final String liquorCategory= liquorUtil.getCodeName(l.getLiquorCategory());
            final SearchDto dto= SearchDto.builder()
                    .liquorId(l.getLiquorId())
                    .name(l.getName())
                    .liquorCategory(liquorCategory)
                    .brewery(l.getBrewery())
                    .imageUrl(l.getImageUrl())
                    .startIdx(l.getName().indexOf(name))
                    .build();
            result.add(dto);
        }

        return result.stream()
                .sorted(Comparator.comparing(SearchDto::getStartIdx)
                        .thenComparing(SearchDto::getName))
                .collect(Collectors.toList());
    }

}
